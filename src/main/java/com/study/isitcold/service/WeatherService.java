package com.study.isitcold.service;

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

    public Weather getWeather(int nx, int ny) throws URISyntaxException {
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String baseDate = now.format(dateFormatter);
        String baseTime = now.format(timeFormatter);

        String url = String.format("%s?serviceKey=%s&pageNo=1&numOfRows=60&dataType=JSON&base_date=%s&base_time=%s&nx=%d&ny=%d",
                API_URL, serviceKey, baseDate, baseTime, nx, ny);

        URI uri = new URI(url);

        System.out.println("Generated URL: " + uri);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
        //

        System.out.println("Response URL: " + uri);
        System.out.println("Response: " + response);

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

        return weather;
    }
}
