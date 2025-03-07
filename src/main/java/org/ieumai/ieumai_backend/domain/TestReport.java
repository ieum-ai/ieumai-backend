package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "test_report")
@IdClass(TestReportId.class)
public class TestReport {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;

    @Id
    @Column(name = "report_version")
    private Integer reportVersion;

    @Column(columnDefinition = "json")
    private String testReport;
}