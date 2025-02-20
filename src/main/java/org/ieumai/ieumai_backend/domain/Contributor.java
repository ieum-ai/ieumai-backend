package org.ieumai.ieumai_backend.domain;

import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.domain.enums.State;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "contributor")
@Builder
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

    @Builder
    public Contributor(String name, String email, String gender, Integer birthyear, State state, City city) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthyear = birthyear;
        this.state = state;
        this.city = city;
    }

    protected Contributor() {}
}
