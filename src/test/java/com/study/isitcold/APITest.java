package com.study.isitcold;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class APITest {
    public static void main(String[] args) throws Exception {

        String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
        String authKey = "8whX6FiBbxmrDTh2tlVPBjx4IyHssnwtvC%2B0yuyBg6T0C0B0Y%2BaIbeOdtkBQZSnBfzHFGEA7C4%2BIjG32N5lHJg%3D%3D";

        String nx = "69";
        String ny = "100";
        String baseDate = "20240716";
        String baseTime = "2130";

        String dataType = "JSON";

        StringBuilder urlBuilder = new StringBuilder(apiURL);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8")+"="+authKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8")+"=1");
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8")+"=60");
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8")+"="+dataType);
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8")+"=20240716");
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8")+"=2020");
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8")+"=55");
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8")+"=127");

        URL url = new URL(urlBuilder.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode()<=300){
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
    }
}
