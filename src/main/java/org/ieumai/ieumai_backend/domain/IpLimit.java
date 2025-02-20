package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ip_limits")
public class IpLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @Setter
    @Column(nullable = false)
    @Builder.Default
    private int count = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @Column(nullable = false)
    private LocalDateTime lastRequestAt;

    @Setter
    private LocalDateTime blockExpiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastRequestAt = LocalDateTime.now();
    }

    public void resetCount() {
        this.count = 0;
        this.blockExpiresAt = null;
    }

    public void incrementCount() {
        this.count++;
        this.lastRequestAt = LocalDateTime.now();
    }

    public void block() {
        this.blockExpiresAt = LocalDateTime.now().plusHours(1);
    }
}
