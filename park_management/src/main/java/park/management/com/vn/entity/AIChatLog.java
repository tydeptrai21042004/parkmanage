package park.management.com.vn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import park.management.com.vn.entity.base.BaseEntity;

@Entity
@Table(name ="ai_chat_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AIChatLog extends BaseEntity {

    @Lob
    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)           // ✅ bắt buộc có user
    @JoinColumn(name = "user_entity_id", nullable = false)
    private UserEntity userEntity;
}
