package com.mknieszner.productconnector.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncProductMessageReaderLeaderTask {
    public static final String TASK_ID = "async-product-message-leader";
    private final LeaderElectionService leaderElectionService;

    @Scheduled(fixedDelay = 10000)
    public void refreshLeaderTask() {
        leaderElectionService.tryAcquireLeadership(TASK_ID);
    }
}

