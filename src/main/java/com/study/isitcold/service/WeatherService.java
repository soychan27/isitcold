package com.study.isitcold.service;

import com.study.isitcold.model.User;
import com.study.isitcold.model.Weather;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {
    private static final String API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";

    @Value("${openApi.serviceKey}")
    private String serviceKey; // 실제 서비스 키로 대체하세요

    public Weather getWeather(int nx, int ny, User user) throws URISyntaxException {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String baseDate = now.format(dateFormatter);
        String baseTime = now.format(timeFormatter);

        String url = String.format("%s?serviceKey=%s&pageNo=1&numOfRows=60&dataType=JSON&base_date=%s&base_time=%s&nx=%d&ny=%d",
                API_URL, serviceKey, baseDate, baseTime, nx, ny);

        URI uri = new URI(url);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);

        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Empty response from API");
        }

        JSONObject jsonObject = new JSONObject(new JSONTokener(response));
        JSONObject responseHeader = jsonObject.getJSONObject("response").getJSONObject("header");
        String resultCode = responseHeader.getString("resultCode");

        if (!"00".equals(resultCode)) {
            String errMsg = responseHeader.getString("resultMsg");
            throw new RuntimeException("API 호출 실패: " + errMsg);
        }

        JSONObject body = jsonObject.getJSONObject("response").getJSONObject("body");
        JSONArray items = body.getJSONObject("items").getJSONArray("item");

        Weather weather = new Weather();
        weather.setDate(baseDate);
        weather.setTime(baseTime);

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String category = item.getString("category");
            String valueStr = item.getString("fcstValue");
            valueStr = valueStr.replaceAll("[^0-9.]", ""); // 숫자와 점만 남기기
            Double value = valueStr.isEmpty() ? null : Double.parseDouble(valueStr);

            switch (category) {
                case "T1H":
                    weather.setT1H(value);
                    break;
                case "RN1":
                    weather.setRN1(value);
                    break;
                case "SKY":
                    weather.setSKY(value);
                    break;
                case "UUU":
                    weather.setUUU(value);
                    break;
                case "VVV":
                    weather.setVVV(value);
                    break;
                case "REH":
                    weather.setREH(value);
                    break;
                case "PTY":
                    weather.setPTY(value);
                    break;
                case "LGT":
                    weather.setLGT(value);
                    break;
                case "VEC":
                    weather.setVEC(value);
                    break;
                case "WSD":
                    weather.setWSD(value);
                    break;
                default:
                    break;
            }
        }

        // 기온(T1H)과 풍속(WSD)을 이용한 체감 온도 계산
        double t1h = weather.getT1H() != null ? weather.getT1H() : 0;
        double wsd = weather.getWSD() != null ? weather.getWSD() : 0;
        double feelsLikeTemp = calculateFeelsLikeTemperature(t1h, wsd);
        weather.setFeelsLikeTemperature(feelsLikeTemp);

        // 불쾌 지수 계산
        double reh = weather.getREH() != null ? weather.getREH() : 0;
        double discomfortIndex = calculateDiscomfortIndex(t1h, reh);
        weather.setDiscomfortTemperature(discomfortIndex);

        // 습도를 고려한 옷차림 추천 설정
        String clothingRecommendation;
        if (reh < 60) {
            clothingRecommendation = getClothingRecommendationBasedOnFeelsLike(feelsLikeTemp, user.getTemp());
        } else {
            clothingRecommendation = getClothingRecommendationBasedOnDiscomfort(discomfortIndex);
        }
        weather.setClothingRecommendation(clothingRecommendation);

        return weather;
    }

    // 체감 온도 계산 함수
    private double calculateFeelsLikeTemperature(double t1h, double wsd) {
        double feelsLikeTemp = 13.12 + 0.6215 * t1h - 11.37 * Math.pow(wsd, 0.16) + 0.3965 * Math.pow(wsd, 0.16) * t1h;
        return Math.round(feelsLikeTemp * 10.0) / 10.0; // 소숫점 둘째 자리에서 반올림
    }

    // 불쾌 지수 계산 함수
    private double calculateDiscomfortIndex(double t1h, double reh) {
        return Math.round((1.8 * t1h - 0.55 * (1 - (reh / 100)) * (1.8 * t1h - 26) + 32) * 10.0) / 10.0;
    }

    // 체감 온도를 기반으로 한 옷차림 추천 함수
    private String getClothingRecommendationBasedOnFeelsLike(double feelsLikeTemp, double userTemp) {
        double tempDiff = userTemp - feelsLikeTemp;
        if (tempDiff >= 3) {
            return "습하지 않으니 체감온도를 통한 추천입니다. 반팔을 입으세요";
        } else if (tempDiff >= 0) {
            return "습하지 않으니 체감온도를 통한 추천입니다. 긴팔을 입으세요";
        } else if (tempDiff >= -3) {
            return "습하지 않으니 체감온도를 통한 추천입니다. 가벼운 외투를 입으세요";
        } else {
            return "습하지 않으니 체감온도를 통한 추천입니다. 따뜻한 외투를 입으세요";
        }
    }

    // 불쾌 지수를 기반으로 한 옷차림 추천 함수
    private String getClothingRecommendationBasedOnDiscomfort(double discomfortIndex) {
        if (discomfortIndex >= 80) {
            return "습하니 불쾌지수를 통한 추천입니다. 시원한 옷을 입으세요";
        } else if (discomfortIndex >= 75) {
            return "습하니 불쾌지수를 통한 추천입니다. 얇은 옷을 입으세요";
        } else {
            return "습하니 불쾌지수를 통한 추천입니다. 편안한 옷을 입으세요";
        }
    }
}
