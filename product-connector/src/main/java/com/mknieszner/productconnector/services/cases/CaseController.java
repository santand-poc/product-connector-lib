package com.mknieszner.productconnector.services.cases;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/case")
@RequiredArgsConstructor
public class CaseController {
    private final CaseStatusService caseStatusService;

    @PostMapping("/{caseId}/resume")
    public ResponseEntity<Void> resumeCase(@PathVariable Long caseId) {
        caseStatusService.resumeCase(caseId);
        return ResponseEntity.ok().build();
    }
}
