package ai.ieum.ieumai_backend.repository;


import ai.ieum.ieumai_backend.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findByUUID(String uuid);
}
