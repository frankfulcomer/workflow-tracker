package com.example.tracker;

import com.example.tracker.model.Status;
import com.example.tracker.model.WorkItem;
import com.example.tracker.repository.WorkItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final WorkItemRepository repository;

    public DataSeeder(WorkItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        seed("Set up staging environment", "Provision and configure the staging box for QA sign-off.", "Frank", Status.IN_PROGRESS);
        seed("Investigate login timeout bug", "Users are getting logged out after ~2 minutes of inactivity.", "Frank", Status.NEW);
        seed("Write regression suite for checkout", "Cover happy path plus 3 known edge cases.", "Frank", Status.NEW);
        seed("Verify data migration script", "Confirm row counts and checksums match between old and new schema.", "Frank", Status.RESOLVED);
    }

    private void seed(String title, String description, String assignee, Status status) {
        WorkItem item = new WorkItem();
        item.setTitle(title);
        item.setDescription(description);
        item.setAssignee(assignee);
        item.setStatus(status);
        repository.save(item);
    }
}
