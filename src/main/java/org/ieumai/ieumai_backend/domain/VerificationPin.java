package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_pins")
public class VerificationPin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    @Builder.Default
    private int attempts = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private static final int MAX_ATTEMPTS = 3;
    private static final int EXPIRY_MINUTES = 10;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusMinutes(EXPIRY_MINUTES);
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public int getRemainingAttempts() {
        return MAX_ATTEMPTS - attempts;
    }

    public boolean hasExceededAttempts() {
        return attempts >= MAX_ATTEMPTS;
    }

    public void incrementAttempts() {
        this.attempts++;
    }
}