package com.mknieszner.productconnector.services.cases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseStatusRepository extends JpaRepository<CaseStatus, Long> {
}
