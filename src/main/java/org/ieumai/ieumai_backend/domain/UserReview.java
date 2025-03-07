package org.ieumai.ieumai_backend.domain;

import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.domain.enums.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "user_review")
public class UserReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_review_id")
    private Long userReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_voice_id")
    private TestScriptVoiceFile testVoice;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private City city;
}