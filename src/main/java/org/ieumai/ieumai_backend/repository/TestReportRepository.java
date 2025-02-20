package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.TestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestReportRepository extends JpaRepository<TestReport, Long> {
    List<TestReport> findByTestId(Long testId);
    Optional<TestReport> findByTestIdAndReportVersion(Long testId, Integer reportVersion);
}