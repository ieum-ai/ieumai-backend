package org.ieumai.ieumai_backend.domain;

import java.io.Serializable;
import java.util.Objects;

public class TestReportId implements Serializable {
    private Long test;
    private Integer reportVersion;

    public TestReportId() {
    }

    public TestReportId(Long test, Integer reportVersion) {
        this.test = test;
        this.reportVersion = reportVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestReportId that = (TestReportId) o;
        return Objects.equals(test, that.test) &&
                Objects.equals(reportVersion, that.reportVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(test, reportVersion);
    }
}
