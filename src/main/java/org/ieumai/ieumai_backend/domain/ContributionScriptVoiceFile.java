package org.ieumai.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "contribution_script_voice_file")
public class ContributionScriptVoiceFile {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contribution_id")
    private Contribution contribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contribution_script_id")
    private ContributionScript contributionScript;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voice_file_id")
    private VoiceFile voiceFile;

    private LocalDateTime createdAt;
}
