package ai.ieum.ieumai_backend.domain;

import ai.ieum.ieumai_backend.domain.enums.City;
import ai.ieum.ieumai_backend.domain.enums.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "user_review")
public class UserReview {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_voice_id")
    private TestScriptVoiceFile testVoice;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private City city;
}