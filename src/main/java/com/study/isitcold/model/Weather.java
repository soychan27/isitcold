package com.study.isitcold.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Data
public class Weather {
    private int locationCode;
    private String date;
    private String time;
    private Double T1H; // 기온
    private Double RN1; // 1시간 강수량
    private Double SKY; // 하늘 상태
    private Double UUU; // 동서바람 성분
    private Double VVV; // 남북바람 성분
    private Double REH; // 습도
    private Double PTY; // 강수형태
    private Double LGT; // 낙뢰
    private Double VEC; // 풍향
    private Double WSD; // 풍속

    private Double feelsLikeTemperature; // 체감 온도
    private String clothingRecommendation; // 옷차림 추천
    private Double discomfortTemperature; // 불쾌 지수
    private String discomfortTempRecommendation; // 불쾌 지수 수치에 따른 추천

    // 날짜 포맷팅 메서드
    public String getFormattedDate() {
        if (date == null) {
            return "";
        }
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return localDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));
    }

    // 시간 포맷팅 메서드
    public String getFormattedTime() {
        if (time == null) {
            return "";
        }
        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
        int hour = localTime.getHour();
        String period = hour < 12 ? "오전" : "오후";
        hour = hour % 12 == 0 ? 12 : hour % 12;
        return String.format("%s %d:%02d", period, hour, localTime.getMinute());
    }

    private String formatValue(Double value) {
        return value == null ? "x" : value.toString();
    }

    @Override
    public String toString() {
        return "locationCode = " + locationCode +
                "\n날짜 = " + getFormattedDate() +
                "\n시간 = " + getFormattedTime() +
                "\n기온 (T1H) = " + formatValue(T1H) +
                "\n1시간 강수량 (RN1) = " + formatValue(RN1) +
                "\n하늘 상태 (SKY) = " + formatValue(SKY) +
                "\n동서바람 성분 (UUU) = " + formatValue(UUU) +
                "\n남북바람 성분 (VVV) = " + formatValue(VVV) +
                "\n습도 (REH) = " + formatValue(REH) +
                "\n강수형태 (PTY) = " + formatValue(PTY) +
                "\n낙뢰 (LGT) = " + formatValue(LGT) +
                "\n풍향 (VEC) = " + formatValue(VEC) +
                "\n풍속 (WSD) = " + formatValue(WSD) +
                "\n체감 온도 = " + formatValue(feelsLikeTemperature) +
                "\n불쾌 지수 = " + formatValue(discomfortTemperature) +
                "\n불쾌 지수에 따른 권장 = " + discomfortTempRecommendation +
                "\n옷차림 추천 = " + clothingRecommendation;
    }
}
