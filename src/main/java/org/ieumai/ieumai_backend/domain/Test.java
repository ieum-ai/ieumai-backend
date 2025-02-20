package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import com.github.f4b6a3.uuid.UuidCreator;

@Getter @Setter
@Entity
@Table(name = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;

    @Column(unique = true, nullable = false, length = 36)
    private String UUID;

    public Test() {
        this.UUID = UuidCreator.getTimeOrderedEpoch().toString();
    }

    @Builder
    public Test(String UUID) {
        this.UUID = (UUID != null) ? UUID : UuidCreator.getTimeOrderedEpoch().toString();
    }

    @PrePersist
    protected void onCreate() {
        if (this.UUID == null) {
            this.UUID = UuidCreator.getTimeOrderedEpoch().toString();
        }
    }

}
