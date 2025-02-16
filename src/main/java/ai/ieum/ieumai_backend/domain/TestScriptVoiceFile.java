package ai.ieum.ieumai_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "test_script_voice_file")
public class TestScriptVoiceFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_script_id")
    private TestScript testScript;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voice_file_id")
    private VoiceFile voiceFile;

    private LocalDateTime createdAt;
}
