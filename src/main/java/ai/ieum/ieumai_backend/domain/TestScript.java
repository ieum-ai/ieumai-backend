package ai.ieum.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "test_script")
public class TestScript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testScriptId;

    private String testScript;
    private Integer testCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
}