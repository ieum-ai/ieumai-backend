package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "test_script")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestScript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testScriptId;

    private String testScript;
    private Integer testCount;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public void incrementTestCount() {
        this.testCount = (this.testCount == null) ? 1 : this.testCount + 1;
    }

    public void deactivate() {
        this.isActive = false;
    }
    
}