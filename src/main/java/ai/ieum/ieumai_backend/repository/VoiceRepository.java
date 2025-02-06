package ai.ieum.ieumai_backend.repository;

import ai.ieum.ieumai_backend.domain.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceRepository extends JpaRepository<Voice, Long> {
    List<Voice> findByUserId(Long userId);
    List<Voice> findByScriptId(Long scriptId);
    List<Voice> findByUserIdAndSource(Long userId, Source source);
    List<Voice> findByScriptIdAndSource(Long scriptId, Source source);
    Long countByUserIdAndSource(Long userId, Source source);
}
