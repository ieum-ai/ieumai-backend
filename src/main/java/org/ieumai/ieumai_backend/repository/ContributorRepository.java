package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContributorRepository extends JpaRepository<Contributor, Long> {
    Optional<Contributor> findByEmail(String email);
}
