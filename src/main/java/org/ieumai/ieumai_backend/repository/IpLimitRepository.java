package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.IpLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IpLimitRepository extends JpaRepository<IpLimit, Long> {
    Optional<IpLimit> findByIp(String ip);
}
