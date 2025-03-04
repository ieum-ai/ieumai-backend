package org.ieumai.ieumai_backend.domain;

import lombok.*;
import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.domain.enums.State;
import jakarta.persistence.*;

@Getter @Setter
@Entity
@Table(name = "contributor")
@NoArgsConstructor
@AllArgsConstructor
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

}
