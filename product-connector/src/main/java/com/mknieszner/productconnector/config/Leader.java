package com.mknieszner.productconnector.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Leader {
    @Id
    private String task_id;

    @Column(nullable = false)
    private String instanceId; // Identyfikator instancji lidera

    @Column(nullable = false)
    private LocalDateTime lockTime; // Czas przejęcia blokady

    @Column(nullable = false)
    private LocalDateTime lockExpiry; // Czas wygaśnięcia blokady

    public static Leader newFrom(String taskId, String instanceId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plus(Duration.ofSeconds(30));
        return Leader.builder()
                .task_id(taskId)
                .instanceId(instanceId)
                .lockTime(now)
                .lockExpiry(expiry)
                .build();
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return getLockExpiry().isBefore(now);
    }

    public void takeOver(String instanceId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plus(Duration.ofSeconds(30));
        setInstanceId(instanceId);
        setLockTime(now);
        setLockExpiry(expiry);
    }

    public void refresh() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plus(Duration.ofSeconds(30));
        setLockExpiry(expiry);
    }
}
