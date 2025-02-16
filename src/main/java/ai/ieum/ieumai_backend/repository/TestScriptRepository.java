package ai.ieum.ieumai_backend.repository;

import ai.ieum.ieumai_backend.domain.TestScript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestScriptRepository extends JpaRepository<TestScript, Long> {
    List<TestScript> findByIsActiveTrue();
}
