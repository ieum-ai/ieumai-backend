package ai.ieum.ieumai_backend.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum City {
    // 강원특별자치도
    강릉시(State.강원특별자치도),
    동해시(State.강원특별자치도),
    삼척시(State.강원특별자치도),
    속초시(State.강원특별자치도),
    원주시(State.강원특별자치도),
    춘천시(State.강원특별자치도),
    태백시(State.강원특별자치도),

    // 경기도
    고양시(State.경기도),
    과천시(State.경기도),
    광명시(State.경기도),
    광주시(State.경기도),
    구리시(State.경기도),
    군포시(State.경기도),
    김포시(State.경기도),
    남양주시(State.경기도),
    동두천시(State.경기도),
    부천시(State.경기도),
    성남시(State.경기도),
    수원시(State.경기도),
    시흥시(State.경기도),
    안산시(State.경기도),
    안성시(State.경기도),
    안양시(State.경기도),
    양주시(State.경기도),
    여주시(State.경기도),
    오산시(State.경기도),
    용인시(State.경기도),
    의왕시(State.경기도),
    의정부시(State.경기도),
    이천시(State.경기도),
    파주시(State.경기도),
    평택시(State.경기도),
    포천시(State.경기도),
    하남시(State.경기도),
    화성시(State.경기도),

    // 경상남도
    거제시(State.경상남도),
    김해시(State.경상남도),
    밀양시(State.경상남도),
    사천시(State.경상남도),
    양산시(State.경상남도),
    진주시(State.경상남도),
    창원시(State.경상남도),
    통영시(State.경상남도),

    // 경상북도
    경산시(State.경상북도),
    경주시(State.경상북도),
    구미시(State.경상북도),
    김천시(State.경상북도),
    문경시(State.경상북도),
    상주시(State.경상북도),
    안동시(State.경상북도),
    영주시(State.경상북도),
    영천시(State.경상북도),
    포항시(State.경상북도),

    // 전라남도
    광양시(State.전라남도),
    나주시(State.전라남도),
    목포시(State.전라남도),
    순천시(State.전라남도),
    여수시(State.전라남도),

    // 전라북도
    군산시(State.전라북도),
    김제시(State.전라북도),
    남원시(State.전라북도),
    익산시(State.전라북도),
    전주시(State.전라북도),
    정읍시(State.전라북도),

    // 제주특별자치도
    서귀포시(State.제주특별자치도),
    제주시(State.제주특별자치도),

    // 충청남도
    계룡시(State.충청남도),
    공주시(State.충청남도),
    논산시(State.충청남도),
    당진시(State.충청남도),
    보령시(State.충청남도),
    서산시(State.충청남도),
    아산시(State.충청남도),
    천안시(State.충청남도),

    // 충청북도
    제천시(State.충청북도),
    청주시(State.충청북도),
    충주시(State.충청북도),

    NONE(null);  // 기본값

    private final State state;

    City(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    // 특정 State에 속한 모든 City 목록 조회
    public static List<City> getCitiesByState(State state) {
        return Arrays.stream(City.values())
                .filter(city -> city.getState() == state)
                .collect(Collectors.toList());
    }

    // City 이름으로 City enum 찾기
    public static City fromString(String cityName) {
        try {
            return City.valueOf(cityName);
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}