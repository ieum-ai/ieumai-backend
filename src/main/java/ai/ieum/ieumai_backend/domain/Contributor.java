package ai.ieum.ieumai_backend.domain;

import ai.ieum.ieumai_backend.domain.enums.City;
import ai.ieum.ieumai_backend.domain.enums.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "contributor")
public class Contributor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contributorId;

    private String name;
    private String email;
    private String gender;
    private Integer birthyear;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private City city;
}
