package org.ieumai.ieumai_backend.util;

import org.ieumai.ieumai_backend.domain.Contributor;
import org.ieumai.ieumai_backend.domain.ContributionScript;
import org.ieumai.ieumai_backend.domain.TestScript;
import org.ieumai.ieumai_backend.domain.VoiceFile;
import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.domain.enums.Source;
import org.ieumai.ieumai_backend.domain.enums.State;

import java.time.LocalDateTime;

public class TestDataFactory {

    public static Contributor createTestContributor() {
        return Contributor.builder()
                .contributorId(1L)
                .name("테스트사용자")
                .email("test@example.com")
                .gender("남성")
                .birthyear(1990)
                .state(State.서울특별시)
                .city(City.valueOf("서울시"))
                .build();
    }

    public static ContributionScript createTestContributionScript() {
        return ContributionScript.builder()
                .contributionScriptId(1L)
                .contributionScript("테스트용 기여 스크립트입니다.")
                .contributionCount(0)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static TestScript createTestTestScript() {
        return TestScript.builder()
                .testScriptId(1L)
                .testScript("테스트용 테스트 스크립트입니다.")
                .testCount(0)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static VoiceFile createTestVoiceFile() {
        return VoiceFile.builder()
                .voiceFileId(1L)
                .voiceLength(5000L) // 5초
                .path("test/1.wav")
                .source(Source.Contribution)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
