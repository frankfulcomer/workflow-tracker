package com.example.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Excluded from JSON: WorkItem.statusHistory -> StatusHistory.workItem would otherwise
    // serialize back into the same WorkItem, recursing forever.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_item_id", nullable = false)
    private WorkItem workItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status newStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkItem getWorkItem() { return workItem; }
    public void setWorkItem(WorkItem workItem) { this.workItem = workItem; }

    public Status getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(Status previousStatus) { this.previousStatus = previousStatus; }

    public Status getNewStatus() { return newStatus; }
    public void setNewStatus(Status newStatus) { this.newStatus = newStatus; }

    public LocalDateTime getChangedAt() { return changedAt; }
}
