package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.domain.enums.State;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {

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
