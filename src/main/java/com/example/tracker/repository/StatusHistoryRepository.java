package com.example.tracker.repository;

import com.example.tracker.model.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByWorkItemIdOrderByChangedAtAsc(Long workItemId);
}
