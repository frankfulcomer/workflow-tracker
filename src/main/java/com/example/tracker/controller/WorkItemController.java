package com.example.tracker.controller;

import com.example.tracker.dto.WorkItemRequest;
import com.example.tracker.model.Status;
import com.example.tracker.model.WorkItem;
import com.example.tracker.service.OwnerNotFoundException;
import com.example.tracker.service.WorkItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class WorkItemController {

    private final WorkItemService service;

    public WorkItemController(WorkItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkItem> list(@RequestParam(required = false) Status status,
                                @RequestParam(required = false) String q) {
        if (status != null) {
            return service.findByStatus(status);
        }
        if (q != null && !q.isBlank()) {
            return service.search(q);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkItem> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody WorkItemRequest request) {
        try {
            WorkItem saved = service.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody WorkItemRequest request) {
        try {
            return ResponseEntity.ok(service.update(id, request));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Status newStatus = Status.valueOf(body.get("status"));
            return ResponseEntity.ok(service.updateStatus(id, newStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/owner")
    public ResponseEntity<?> updateOwner(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        try {
            Long ownerId = body.get("ownerId");
            return ResponseEntity.ok(service.changeOwner(id, ownerId));
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
