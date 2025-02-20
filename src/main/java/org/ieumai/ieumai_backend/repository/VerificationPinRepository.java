package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.VerificationPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationPinRepository extends JpaRepository<VerificationPin, Long> {
    Optional<VerificationPin> findByEmailAndExpiresAtGreaterThan(String email, LocalDateTime now);
    void deleteByEmail(String email);
}
