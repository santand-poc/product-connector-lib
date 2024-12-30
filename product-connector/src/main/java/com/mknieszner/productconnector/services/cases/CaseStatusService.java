package com.mknieszner.productconnector.services.cases;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaseStatusService {
    private final CaseStatusRepository caseStatusRepository;
    @Value("${case.pause.enabled:true}")
    private boolean isPauseEnabled;
    public boolean isCasePaused(Long caseId) {
        if (!isPauseEnabled) {
            return false;
        }
        return caseStatusRepository.findById(caseId)
                .map(CaseStatus::getStatus)
                .map(status -> status.equals("PAUSED"))
                .orElse(false);
    }

    public void pauseCase(Long caseId) {
        if (!isPauseEnabled) {
            return;
        }
        caseStatusRepository.save(CaseStatus.paused(caseId));
    }

    public void resumeCase(Long caseId) {
        if (!isPauseEnabled) {
            return;
        }
        caseStatusRepository.save(CaseStatus.active(caseId));
    }
}
