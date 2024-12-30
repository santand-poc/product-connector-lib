package com.mknieszner.productconnector.services.cases;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_status")
public class CaseStatus {
    @Id
    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static CaseStatus active(Long caseId) {
        return CaseStatus.builder()
                .caseId(caseId)
                .status("ACTIVE")
                .build();
    }

    public static CaseStatus paused(Long caseId) {
        return CaseStatus.builder()
                .caseId(caseId)
                .status("PAUSED")
                .build();
    }

    @PrePersist
    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
