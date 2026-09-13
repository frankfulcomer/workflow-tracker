# Workflow Tracker — Product Stories

These lightweight Agile-style user stories and acceptance criteria define the behavior for the next Workflow Tracker increment.

They are intended to provide a shared basis for development, manual testing, and automated regression testing.

## WF-001 — Assign an Owner to a Work Item

### User Story

As a user, I want to assign a work item to an owner so that responsibility for the item is clear.

### Acceptance Criteria

- **AC-1:** A work item may be assigned to an existing owner when it is created.
- **AC-2:** A work item may be created without an assigned owner.
- **AC-3:** The assigned owner is displayed on the work-item list.
- **AC-4:** The assigned owner is displayed on the work-item detail view.
- **AC-5:** The owner can be changed after the work item is created.
- **AC-6:** A work item may be changed from assigned to unassigned.
- **AC-7:** When a work item is created, the system records its creation timestamp.
- **AC-8:** When a work item is modified, the system records the timestamp of the most recent modification.

---

## WF-002 — Manage Work-Item Status

### User Story

As a user, I want work items to move through a defined lifecycle so that their current state is clear.

### Status Definitions

- **NEW** — The work item has been created but has not yet been reviewed or accepted into the active work queue.
- **OPEN** — The work item has been reviewed and is available for active work.
- **IN_PROGRESS** — Work is actively being performed on the item.
- **RESOLVED** — Work is believed to be complete and is awaiting confirmation or closure.
- **CLOSED** — The work item is finished and no further action is expected.

### Acceptance Criteria

- **AC-1:** Supported statuses are `NEW`, `OPEN`, `IN_PROGRESS`, `RESOLVED`, and `CLOSED`.
- **AC-2:** A newly created work item starts in `NEW`.
- **AC-3:** `NEW → OPEN` is allowed.
- **AC-4:** `OPEN → IN_PROGRESS` is allowed.
- **AC-5:** `IN_PROGRESS → RESOLVED` is allowed.
- **AC-6:** `RESOLVED → CLOSED` is allowed.
- **AC-7:** `IN_PROGRESS → OPEN` is allowed.
- **AC-8:** `RESOLVED → IN_PROGRESS` is allowed.
- **AC-9:** `RESOLVED → OPEN` is allowed.
- **AC-10:** `CLOSED → OPEN` is allowed.
- **AC-11:** Any status transition not explicitly allowed above is rejected.
- **AC-12:** A rejected status transition leaves the current status unchanged.
- **AC-13:** Selecting the work item's current status again is treated as a no-op: the status remains unchanged, no error is displayed, and no status-history record is created.
- **AC-14:** When a status transition is rejected, the system displays an error message indicating that the transition is not allowed.

---

## WF-003 — Preserve Status History

### User Story

As a user, I want to see the history of status changes so that I can understand how a work item progressed.

### Acceptance Criteria

- **AC-1:** Every successful status change creates a history record.
- **AC-2:** Each history record contains the previous status, new status, and change timestamp.
- **AC-3:** Each history record is associated with the correct work item.
- **AC-4:** Rejected status transitions do not create history records.
- **AC-5:** Existing status history is preserved when a work item is reopened.
- **AC-6:** Status history is displayed on the work-item detail view.
- **AC-7:** Creating a work item does not create a status-history record. Status history begins with the first successful change from the initial `NEW` status.

## Scope Notes

- Owners are predefined for this increment; creating, editing, or deleting owners is out of scope.
- Owner assignment and status history are the focus of this increment.
- Existing search and filtering behavior remains unchanged.
- Authentication, roles and permissions, comments, notifications, reporting dashboards, and other product features are out of scope.