package com.foodbridge.entity;
import jakarta.persistence.*;import lombok.*;import java.time.*;
@Entity @Getter @Setter @NoArgsConstructor public class ConsumerResetCode {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String email;
    @Column(nullable=false) private String codeHash;
    private LocalDateTime expiresAt;private boolean used;
}
