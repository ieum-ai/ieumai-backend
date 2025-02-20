package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    List<Contribution> findByContributorId(Long contributorId);
}
