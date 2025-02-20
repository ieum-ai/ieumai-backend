package org.ieumai.ieumai_backend.domain;

import org.ieumai.ieumai_backend.domain.enums.Source;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "voice_file")
public class VoiceFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voiceFileId;

    private Long voiceLength;
    private String path;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Builder
    public VoiceFile(Long voiceLength, String path, Source source) {
        this.voiceLength = voiceLength;
        this.path = path;
        this.source = source;
    }

    protected VoiceFile() {}

}
