package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.domain.TestScriptVoiceFile;
import org.ieumai.ieumai_backend.repository.TestScriptVoiceFileRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestScriptVoiceFileService {
    private final TestScriptVoiceFileRepository testScriptVoiceFileRepository;
    private final TestScriptService testScriptService;

    public TestScriptVoiceFile save(TestScriptVoiceFile voiceFile) {
        // test_script의 test_count 증가
        testScriptService.incrementTestCount(voiceFile.getTestScript().getTestScriptId());

        // 음성 파일 저장
        return testScriptVoiceFileRepository.save(voiceFile);
    }
}
