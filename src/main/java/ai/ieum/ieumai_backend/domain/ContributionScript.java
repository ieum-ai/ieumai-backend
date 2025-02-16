package ai.ieum.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "contribution_script")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionScript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contributionScriptId;

    @Column(nullable = false)
    private String script;

    private Integer contributionCount;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public void incrementContributionCount() {
        this.contributionCount = (this.contributionCount == null) ? 1 : this.contributionCount + 1;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
