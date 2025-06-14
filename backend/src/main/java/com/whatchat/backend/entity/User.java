package com.whatchat.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "publicId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 24)
    private String displayName;

    @Column(length = 240)
    private String statusText;

    @Lob
    private byte[] avatar; // opsiyonel: avatar URL olarak da tutulabilir

    @Column(nullable = false, unique = true)
    private String publicId;

    private boolean isOnline;

    private Instant lastSeen;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @PrePersist
    public void generatePublicId() {
        if (this.publicId == null) {
             this.publicId=UUID.randomUUID()
                    .toString()
                    .replaceAll("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        }
    }
}