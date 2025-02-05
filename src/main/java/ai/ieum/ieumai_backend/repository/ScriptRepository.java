package ai.ieum.ieumai_backend.repository;

import ai.ieum.ieumai_backend.domain.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findTop10ByIsActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT COUNT(s) FROM Script s WHERE s.isActive = true")
    long countActiveScripts();
}
