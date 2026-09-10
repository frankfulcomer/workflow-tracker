package com.example.tracker.service;

import com.example.tracker.model.Status;
import com.example.tracker.model.WorkItem;
import com.example.tracker.repository.WorkItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkItemService {

    private final WorkItemRepository repository;

    public WorkItemService(WorkItemRepository repository) {
        this.repository = repository;
    }

    public List<WorkItem> findAll() {
        return repository.findAll();
    }

    public List<WorkItem> findByStatus(Status status) {
        return repository.findByStatus(status);
    }

    public List<WorkItem> search(String titleFragment) {
        return repository.findByTitleContainingIgnoreCase(titleFragment);
    }

    public Optional<WorkItem> findById(Long id) {
        return repository.findById(id);
    }

    public WorkItem create(WorkItem item) {
        item.setId(null);
        if (item.getStatus() == null) {
            item.setStatus(Status.NEW);
        }
        return repository.save(item);
    }

    /**
     * Enforces a simple, demonstrable workflow rule: an item can't jump
     * straight from NEW to CLOSED - it has to pass through RESOLVED first.
     * This exists mainly so the automation suite has real business logic
     * to exercise, not just CRUD plumbing.
     */
    public WorkItem updateStatus(Long id, Status newStatus) {
        WorkItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No work item with id " + id));

        if (item.getStatus() == Status.NEW && newStatus == Status.CLOSED) {
            throw new IllegalStateException("A NEW item must move to IN_PROGRESS or RESOLVED before it can be CLOSED");
        }

        item.setStatus(newStatus);
        return repository.save(item);
    }

    public WorkItem update(Long id, WorkItem updated) {
        WorkItem existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No work item with id " + id));
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setAssignee(updated.getAssignee());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
