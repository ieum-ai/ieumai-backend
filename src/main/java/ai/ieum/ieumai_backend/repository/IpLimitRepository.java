package ai.ieum.ieumai_backend.repository;

import ai.ieum.ieumai_backend.domain.IpLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IpLimitRepository extends JpaRepository<IpLimit, Long> {
    Optional<IpLimit> findByIp(String ip);
}
