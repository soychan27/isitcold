package com.study.isitcold.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Weather {
    private int locationCode;
    private String date;
    private String time;
    private Double T1H; //기온
    private Double RN1; //1시간 강수량
    private Double SKY; //하늘 상태
    private Double UUU; //동서바람 성분
    private Double VVV; //남북바람 성분
    private Double REH; //습도
    private Double PTY; //강수형태
    private Double LGT; //낙뢰
    private Double VEC; //풍향
    private Double WSD; //풍속

    private String formatValue(Double value) {
        return value == null ? "x" : value.toString();
    }

    @Override
    public String toString() {
        return "locationCode = " + locationCode +
                "\n날짜 = " + date +
                "\n시간 = " + time +
                "\n기온 (T1H) = " + formatValue(T1H) +
                "\n1시간 강수량 (RN1) = " + formatValue(RN1) +
                "\n하늘 상태 (SKY) = " + formatValue(SKY) +
                "\n동서바람 성분 (UUU) = " + formatValue(UUU) +
                "\n남북바람 성분 (VVV) = " + formatValue(VVV) +
                "\n습도 (REH) = " + formatValue(REH) +
                "\n강수형태 (PTY) = " + formatValue(PTY) +
                "\n낙뢰 (LGT) = " + formatValue(LGT) +
                "\n풍향 (VEC) = " + formatValue(VEC) +
                "\n풍속 (WSD) = " + formatValue(WSD);
    }
}
