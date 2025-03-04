package org.ieumai.ieumai_backend.domain;

import lombok.*;
import org.ieumai.ieumai_backend.domain.enums.Source;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}
