package com.example.tracker.service;

import com.example.tracker.dto.WorkItemRequest;
import com.example.tracker.model.Owner;
import com.example.tracker.model.Status;
import com.example.tracker.model.StatusHistory;
import com.example.tracker.model.WorkItem;
import com.example.tracker.repository.OwnerRepository;
import com.example.tracker.repository.StatusHistoryRepository;
import com.example.tracker.repository.WorkItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class WorkItemService {

    /**
     * Allowed status transitions per WF-002 AC-3..AC-11. Anything not listed here
     * (including same-status, handled separately in updateStatus) is rejected.
     */
    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = Map.of(
            Status.NEW, Set.of(Status.OPEN, Status.IN_PROGRESS),
            Status.OPEN, Set.of(Status.IN_PROGRESS),
            Status.IN_PROGRESS, Set.of(Status.RESOLVED, Status.OPEN),
            Status.RESOLVED, Set.of(Status.CLOSED, Status.IN_PROGRESS, Status.OPEN),
            Status.CLOSED, Set.of(Status.OPEN)
    );

    private final WorkItemRepository repository;
    private final OwnerRepository ownerRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public WorkItemService(WorkItemRepository repository, OwnerRepository ownerRepository,
                            StatusHistoryRepository statusHistoryRepository) {
        this.repository = repository;
        this.ownerRepository = ownerRepository;
        this.statusHistoryRepository = statusHistoryRepository;
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

    public WorkItem create(WorkItemRequest request) {
        WorkItem item = new WorkItem();
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setOwner(resolveOwner(request.getOwnerId()));
        // A newly created work item always starts in NEW - no client-supplied status
        // can bypass the state machine at creation time.
        item.setStatus(Status.NEW);
        return repository.save(item);
    }

    /**
     * Enforces the WF-002 status lifecycle: only the transitions in ALLOWED_TRANSITIONS
     * are permitted. Re-selecting the current status is a no-op (no save, no history).
     * On a legal transition, records a StatusHistory row in the same transaction as the
     * status update so the two writes succeed or fail together.
     */
    @Transactional
    public WorkItem updateStatus(Long id, Status newStatus) {
        WorkItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No work item with id " + id));

        Status currentStatus = item.getStatus();
        if (newStatus == currentStatus) {
            return item;
        }

        Set<Status> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + currentStatus + " to " + newStatus);
        }

        item.setStatus(newStatus);
        WorkItem saved = repository.save(item);

        StatusHistory history = new StatusHistory();
        history.setWorkItem(saved);
        history.setPreviousStatus(currentStatus);
        history.setNewStatus(newStatus);
        statusHistoryRepository.save(history);

        return saved;
    }

    public WorkItem update(Long id, WorkItemRequest request) {
        WorkItem existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No work item with id " + id));
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setOwner(resolveOwner(request.getOwnerId()));
        return repository.save(existing);
    }

    /**
     * Changes (or clears, when ownerId is null) a work item's owner. Unlike status,
     * there is no legality state machine here - any predefined owner, or no owner at
     * all, is always a valid target.
     */
    public WorkItem changeOwner(Long id, Long ownerId) {
        WorkItem item = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No work item with id " + id));
        item.setOwner(resolveOwner(ownerId));
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private Owner resolveOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("No owner with id " + ownerId));
    }
}
