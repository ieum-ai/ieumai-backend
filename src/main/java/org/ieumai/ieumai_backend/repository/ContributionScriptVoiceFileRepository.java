package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.ContributionScriptVoiceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionScriptVoiceFileRepository extends JpaRepository<ContributionScriptVoiceFile, Long> {

    List<ContributionScriptVoiceFile> findByContribution_ContributionId(Long contributionId);
    List<ContributionScriptVoiceFile> findByContributionScript_ContributionScriptId(Long contributionScriptId);
}
