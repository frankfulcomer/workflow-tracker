package com.example.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class WorkItemRequest {

    @NotBlank
    private String title;

    private String description;

    private Long ownerId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
