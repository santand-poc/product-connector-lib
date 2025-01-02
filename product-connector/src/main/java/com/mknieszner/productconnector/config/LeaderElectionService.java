package com.mknieszner.productconnector.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final RabbitListenerEndpointRegistry registry;
    private final String productQueueListenerName;

    @Value("${rabbitmq.custom-leader-election:true}")
    private boolean customLeaderElection;

    public boolean isCurrentInstanceLeader(String taskId) {
        if (!customLeaderElection) {
            return true;
        }
        return repository.findById(taskId)
                .map(leader -> INSTANCE_ID.equals(leader.getInstanceId()))
                .orElse(false);
    }

    public void tryAcquireLeadership(String taskId) {
        if (!customLeaderElection) {
            return;
        }
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
            stopListeningForProductEvents();
            System.out.println("Instancja " + INSTANCE_ID + " NIE została liderem dla zadania " + taskId);
        }
    }

    private void refreshLeadership(String taskId) {
        Leader leader = repository.getReferenceById(taskId);
        leader.refresh();
        repository.save(leader);
        startListeningForProductEvents();
        System.out.println("Blokada lidera została odświeżona przez instancję " + INSTANCE_ID);
    }

    private void setLeadership(String taskId) {
        if (repository.existsById(taskId)) {
            throw new DataIntegrityViolationException("Leadership already exists for taskId: " + taskId);
        }
        Leader leader = Leader.newFrom(taskId, INSTANCE_ID);
        repository.save(leader);
        startListeningForProductEvents();
        System.out.println("Instancja " + INSTANCE_ID + " została liderem dla zadania " + taskId);
    }

    private void takeOverLeadership(Leader existingLeader, String taskId) {
        existingLeader.takeOver(INSTANCE_ID);
        repository.save(existingLeader);
        startListeningForProductEvents();
        System.out.println("Instancja " + INSTANCE_ID + " przejęła wygasłą blokadę dla zadania " + taskId);
    }

    public void startListeningForProductEvents() {
        MessageListenerContainer container = registry.getListenerContainer(productQueueListenerName);
        if (container == null) {
            System.out.println("Listener not found.");
            return;
        }
        if (!container.isRunning()) {
            container.start();
            System.out.println("Listener started.");
        } else {
            System.out.println("Listener is already running");
        }
    }

    public void stopListeningForProductEvents() {
        MessageListenerContainer container = registry.getListenerContainer(productQueueListenerName);
        if (container == null) {
            System.out.println("Listener not found.");
            return;
        }
        if (container.isRunning()) {
            container.stop();
            System.out.println("Listener stopped.");
        } else {
            System.out.println("Listener is already stopped");
        }
    }
}
