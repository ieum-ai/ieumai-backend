package ai.ieum.ieumai_backend.domain;

import ai.ieum.ieumai_backend.domain.enums.Source;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "voices")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voice_id")
    private Long voiceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "script_id", nullable = false)
    private Long scriptId;

    @Column(nullable = false)
    private Double duration;

    @Column(length = 100)
    private String path;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
