package ai.ieum.ieumai_backend.domain;

import ai.ieum.ieumai_backend.domain.enums.Gender;
import ai.ieum.ieumai_backend.domain.enums.State;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer birthyear;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(length = 100)
    private String city;
}
