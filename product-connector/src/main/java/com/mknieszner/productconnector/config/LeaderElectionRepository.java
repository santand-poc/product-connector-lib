package com.mknieszner.productconnector.config;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderElectionRepository extends JpaRepository<Leader, String> {
}
