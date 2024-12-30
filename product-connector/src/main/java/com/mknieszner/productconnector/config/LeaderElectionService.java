package com.mknieszner.productconnector.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LeaderElectionService {
    static final String INSTANCE_ID = UUID.randomUUID().toString();
    private final LeaderElectionRepository repository;

    public boolean isCurrentInstanceLeader(String taskId) {
        return repository.findById(taskId)
                .map(leader -> INSTANCE_ID.equals(leader.getInstanceId()))
                .orElse(false);
    }

    public void tryAcquireLeadership(String taskId) {
        if (isCurrentInstanceLeader(taskId)) {
            refreshLeadership(taskId);
        } else {
            acquireLeadership(taskId);
        }
    }

    private void acquireLeadership(String taskId) {
        try {
            setLeadership(taskId);
        } catch (DataIntegrityViolationException e) {
            Leader existingLeader = repository.getReferenceById(taskId);
            if (existingLeader.isExpired()) {
                takeOverLeadership(existingLeader, taskId);
                return;
            }
            System.out.println("Instancja " + INSTANCE_ID + " NIE została liderem dla zadania " + taskId);
        }
    }

    private void refreshLeadership(String taskId) {
        Leader leader = repository.getReferenceById(taskId);
        leader.refresh();
        repository.save(leader);
        System.out.println("Blokada lidera została odświeżona przez instancję " + INSTANCE_ID);
    }

    private void setLeadership(String taskId) {
        if (repository.existsById(taskId)) {
            throw new DataIntegrityViolationException("Leadership already exists for taskId: " + taskId);
        }
        Leader leader = Leader.newFrom(taskId, INSTANCE_ID);
        repository.save(leader);
        System.out.println("Instancja " + INSTANCE_ID + " została liderem dla zadania " + taskId);
    }

    private void takeOverLeadership(Leader existingLeader, String taskId) {
        existingLeader.takeOver(INSTANCE_ID);
        repository.save(existingLeader);
        System.out.println("Instancja " + INSTANCE_ID + " przejęła wygasłą blokadę dla zadania " + taskId);
    }
}
