package ai.ieum.ieumai_backend.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "scripts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "script_id")
    private Long scriptId;

    @Type(JsonType.class)
    @Column(name = "script", columnDefinition = "json")
    private String script;

    @Column(name = "script_count")
    private Long scriptCount;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        scriptCount = 0L;
        isActive = true;
    }

    public void incrementScriptCount() {
        this.scriptCount++;
        if (this.scriptCount >= 50) {
            this.isActive = false;
        }
    }
}
