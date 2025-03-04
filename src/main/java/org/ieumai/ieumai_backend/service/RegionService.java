package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.domain.enums.State;
import org.ieumai.ieumai_backend.dto.RegionResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegionService {

    // 모든 지역 정보 조회
    public RegionResponse getAllRegions() {
        List<String> states = getAllStates();

        Map<String, List<String>> citiesByState = new HashMap<>();

        // 각 도별 시 목록 구성
        for (String state : states) {
            List<String> cities = getCitiesByState(state);
            citiesByState.put(state, cities);
        }

        return new RegionResponse(states, citiesByState);
    }

    // 모든 State 목록 조회
    public List<String> getAllStates() {
        return Arrays.stream(State.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // 특정 State에 속한 City 목록 조회
    public List<String> getCitiesByState(String stateName) {
        try {
            State state = State.valueOf(stateName);
            return City.getCitiesByState(state)
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
}