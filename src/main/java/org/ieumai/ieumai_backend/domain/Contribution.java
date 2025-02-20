package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "contribution")
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contributionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id")
    private Contributor contributor;
}
