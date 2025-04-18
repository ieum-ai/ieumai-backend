package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.TestScriptVoiceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestScriptVoiceFileRepository extends JpaRepository<TestScriptVoiceFile, Long> {

    List<TestScriptVoiceFile> findByTest_TestId(Long testId);
    List<TestScriptVoiceFile> findByTestScript_TestScriptId(Long testScriptId);
}