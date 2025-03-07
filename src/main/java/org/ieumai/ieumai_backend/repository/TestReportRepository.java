package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.TestReport;
import org.ieumai.ieumai_backend.domain.TestReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestReportRepository extends JpaRepository<TestReport, TestReportId> {
    List<TestReport> findByTest_TestId(Long testId);
    Optional<TestReport> findByTest_TestIdAndReportVersion(Long testId, Integer reportVersion);
}