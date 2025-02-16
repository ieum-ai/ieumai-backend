package ai.ieum.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "test_report")
public class TestReport {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;

    @Column(columnDefinition = "json")
    private String testReport;

    private Integer reportVersion;
}
