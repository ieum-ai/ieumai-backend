package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.domain.ContributionScriptVoiceFile;
import org.ieumai.ieumai_backend.repository.ContributionScriptVoiceFileRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContributionScriptVoiceFileService {
    private final ContributionScriptVoiceFileRepository contributionScriptVoiceFileRepository;
    private final ContributionScriptService contributionScriptService;

    public ContributionScriptVoiceFile save(ContributionScriptVoiceFile voiceFile) {
        // contribution_script의 contribution_count 증가
        contributionScriptService.incrementContributionCount(voiceFile.getContributionScript().getContributionScriptId());

        // 음성 파일 저장
        return contributionScriptVoiceFileRepository.save(voiceFile);
    }
}
