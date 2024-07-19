package com.study.isitcold;

import com.study.isitcold.model.Weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONIntegrateTest {
    enum WeatherValue {
        PTY, REH, RN1, T1H, UUU, VEC, VVV, WSD, LGT, SKY
    }

    public static void main(String[] args) throws Exception {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // 현재 시각을 기준으로 1시간 전의 시각을 계산
        LocalDateTime now = LocalDateTime.now().minusHours(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String baseDate = now.format(dateFormatter);
        String baseTime = now.format(timeFormatter);

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.print("Enter the nx value: ");
        String nx = scanner.nextLine();

        System.out.print("Enter the ny value: ");
        String ny = scanner.nextLine();

        // Weather 객체 생성
        Weather weather = new Weather();

        // API URL 및 인증 키
        String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
        String authKey = "8whX6FiBbxmrDTh2tlVPBjx4IyHssnwtvC%2B0yuyBg6T0C0B0Y%2BaIbeOdtkBQZSnBfzHFGEA7C4%2BIjG32N5lHJg%3D%3D";

        String dataType = "JSON";

        // URL 빌더 생성 및 파라미터 추가
        StringBuilder urlBuilder = new StringBuilder(apiURL);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + authKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("60", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

        // URL 출력
        URL url = new URL(urlBuilder.toString());
        System.out.println("Request URL: " + url);

        // HTTP 연결 설정 및 요청 보내기
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        // 응답 처리
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String result = sb.toString();

        System.out.println(result);

        try {
            // JSON 파싱
            JSONObject jsonObject = new JSONObject(new JSONTokener(result));
            JSONObject parse_response = jsonObject.getJSONObject("response");
            JSONObject parse_body = parse_response.getJSONObject("body");
            JSONObject parse_items = parse_body.getJSONObject("items");
            JSONArray parse_item = parse_items.getJSONArray("item");
            System.out.println("--------------------------");

            JSONObject object;
            String category;
            String valueStr;
            Double value;

            // JSON 배열 반복
            for (int temp = 0; temp < parse_item.length(); temp++) {
                object = parse_item.getJSONObject(temp);
                category = object.getString("category");
                valueStr = object.getString("fcstValue");

                // Handle different value formats
                valueStr = valueStr.replaceAll("[^0-9.]", ""); // 숫자와 점만 남기기
                value = valueStr.isEmpty() ? null : Double.parseDouble(valueStr);

                WeatherValue weatherValue = WeatherValue.valueOf(category);

                switch (weatherValue) {
                    case PTY:
                        weather.setPTY(value);
                        break;
                    case REH:
                        weather.setREH(value);
                        break;
                    case RN1:
                        weather.setRN1(value);
                        break;
                    case T1H:
                        weather.setT1H(value);
                        break;
                    case UUU:
                        weather.setUUU(value);
                        break;
                    case VEC:
                        weather.setVEC(value);
                        break;
                    case VVV:
                        weather.setVVV(value);
                        break;
                    case WSD:
                        weather.setWSD(value);
                        break;
                    case LGT:
                        weather.setLGT(value);
                        break;
                    case SKY:
                        weather.setSKY(value);
                        break;
                    default:
                        break;
                }
            }
            weather.setDate(baseDate);
            weather.setTime(baseTime);

            // Weather 객체 출력
            System.out.println(weather);
        } catch (Exception e) {
            System.out.println("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
