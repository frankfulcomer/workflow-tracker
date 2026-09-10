package com.example.tracker.repository;

import com.example.tracker.model.Status;
import com.example.tracker.model.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
    List<WorkItem> findByStatus(Status status);
    List<WorkItem> findByTitleContainingIgnoreCase(String title);
}
