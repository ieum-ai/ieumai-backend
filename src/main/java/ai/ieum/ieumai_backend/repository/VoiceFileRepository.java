package ai.ieum.ieumai_backend.repository;

import ai.ieum.ieumai_backend.domain.VoiceFile;
import ai.ieum.ieumai_backend.domain.enums.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceFileRepository extends JpaRepository<VoiceFile, Long> {
    List<VoiceFile> findByUserId(Long userId);
    List<VoiceFile> findByScriptId(Long scriptId);
    List<VoiceFile> findBySource(Source source);
}
