package com.mknieszner.productconnector.config;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LeaderElectionTest {

    @Test
    void shouldCreateNewLeaderElectionWithCorrectValues() {
        // Given
        String taskId = "task1";
        String instanceId = "instance1";

        // When
        Leader leaderElection = Leader.newFrom(taskId, instanceId);

        // Then
        assertThat(leaderElection.getTask_id()).isEqualTo(taskId);
        assertThat(leaderElection.getInstanceId()).isEqualTo(instanceId);
        assertThat(leaderElection.getLockTime()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(leaderElection.getLockExpiry()).isAfter(leaderElection.getLockTime());
    }

    @Test
    void shouldIdentifyExpiredLock() {
        // Given
        String taskId = "task1";
        String instanceId = "instance1";
        Leader leaderElection = Leader.newFrom(taskId, instanceId);
        leaderElection.setLockExpiry(LocalDateTime.now().minus(Duration.ofSeconds(1)));

        // When
        boolean expired = leaderElection.isExpired();

        // Then
        assertThat(expired).isTrue();
    }

    @Test
    void shouldIdentifyNonExpiredLock() {
        // Given
        String taskId = "task1";
        String instanceId = "instance1";
        Leader leaderElection = Leader.newFrom(taskId, instanceId);

        // When
        boolean expired = leaderElection.isExpired();

        // Then
        assertThat(expired).isFalse();
    }

    @Test
    void shouldTakeOverLeadership() {
        // Given
        String taskId = "task1";
        String currentInstanceId = "currentInstance";
        String newInstanceId = "newInstance";
        Leader leaderElection = Leader.newFrom(taskId, currentInstanceId);

        // When
        leaderElection.takeOver(newInstanceId);

        // Then
        assertThat(leaderElection.getInstanceId()).isEqualTo(newInstanceId);
        assertThat(leaderElection.getLockTime()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(leaderElection.getLockExpiry()).isAfter(leaderElection.getLockTime());
    }

    @SneakyThrows
    @Test
    void shouldRefreshLeadership() {
        // Given
        String taskId = "task1";
        String instanceId = "instance1";
        Leader leaderElection = Leader.newFrom(taskId, instanceId);
        LocalDateTime originalLockExpiry = leaderElection.getLockExpiry();

        // When
        Thread.sleep(1);
        leaderElection.refresh();

        // Then
        assertThat(leaderElection.getLockExpiry()).isAfter(originalLockExpiry);
    }
}
