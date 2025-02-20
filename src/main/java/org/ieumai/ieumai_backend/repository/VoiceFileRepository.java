package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.VoiceFile;
import org.ieumai.ieumai_backend.domain.enums.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceFileRepository extends JpaRepository<VoiceFile, Long> {
    List<VoiceFile> findByUserId(Long userId);
    List<VoiceFile> findByScriptId(Long scriptId);
    List<VoiceFile> findBySource(Source source);
}
