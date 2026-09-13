# Workflow Tracker — Product Stories

These lightweight Agile-style user stories and acceptance criteria define the behavior for the next Workflow Tracker increment.

They are intended to provide a shared basis for development, manual testing, and automated regression testing.

User-interface behavior defined by these stories follows the shared conventions in `docs/ux-conventions.md` unless a story explicitly specifies otherwise.

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
- **AC-9:** A work item that has no assigned owner has no stored owner relationship.

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
- **AC-4:** `NEW → IN_PROGRESS` is allowed.
- **AC-5:** `OPEN → IN_PROGRESS` is allowed.
- **AC-6:** `IN_PROGRESS → RESOLVED` is allowed.
- **AC-7:** `RESOLVED → CLOSED` is allowed.
- **AC-8:** `IN_PROGRESS → OPEN` is allowed.
- **AC-9:** `RESOLVED → IN_PROGRESS` is allowed.
- **AC-10:** `RESOLVED → OPEN` is allowed.
- **AC-11:** `CLOSED → OPEN` is allowed.
- **AC-12:** Any status transition not explicitly allowed above is rejected.
- **AC-13:** A rejected status transition leaves the current status unchanged.
- **AC-14:** Selecting the work item's current status again is treated as a no-op: the status remains unchanged, no error is displayed, and no status-history record is created.
- **AC-15:** When a status transition is rejected, the system displays an error message indicating that the transition is not allowed.

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
- **AC-7:** Creating a work item creates an initial status-history record showing that the work item entered NEW status at the time of creation.

---

## WF-004 — Update a Work-Item Description

### User Story

As a user, I want to update a work item's description so that I can keep its details accurate as the work evolves.

### Acceptance Criteria

- **AC-1:** The work item's description is displayed on the work-item detail view.
- **AC-2:** The description can be edited in the work-item detail view after the work item is created.
- **AC-3:** Saving a changed description updates the work item's modification timestamp.
- **AC-4:** Changing the description does not create a status-history record.

---

## WF-005 — Save Work-Item Changes

### User Story

As a user, I want to review and save changes to a work item as a single edit so that I can control when my changes are committed and avoid accidentally losing them.

### Acceptance Criteria

- **AC-1:** The work-item detail view provides a single `Save Changes` button for editable work-item fields.
- **AC-2:** `Save Changes` is disabled when the detail view is first opened and no values have been changed.
- **AC-3:** Changing the title, owner, or description enables `Save Changes`.
- **AC-4:** If all edited fields are returned to their original values, `Save Changes` becomes disabled again.
- **AC-5:** Selecting `Save Changes` persists all changed editable fields together.
- **AC-6:** After a successful save, the work-item list reflects the saved values and modification timestamp, and the detail view closes.
- **AC-7:** Changing the title, owner, or description does not create a status-history record.
- **AC-8:** Closing the detail view with no unsaved changes closes it immediately.
- **AC-9:** Attempting to close the detail view with unsaved changes prompts the user to confirm whether the changes should be discarded.
- **AC-10:** Canceling the discard confirmation leaves the detail view open with the unsaved changes intact.
- **AC-11:** Confirming the discard closes the detail view without saving the unsaved changes.
- **AC-12:** The description field has a consistent fixed size appropriate for the detail view and cannot be manually resized.
- **AC-13:** The Close and Save Changes controls are grouped in a visually separated action area at the bottom-right of the detail view, with Save Changes presented as the primary action.

---

## WF-006 — Create a Work Item

### User Story

As a user, I want to create a work item with its initial details so that new work can be recorded and tracked.

### Acceptance Criteria

- **AC-1:** A work item requires a nonblank title.
- **AC-2:** A work item may be created with or without a description.
- **AC-3:** A work item may be created with an existing owner or left unassigned.
- **AC-4:** The Create action is disabled until a nonblank title has been entered.
- **AC-5:** Selecting Create with valid input creates one work item using the entered title, description, and owner.
- **AC-6:** A successfully created work item starts in `NEW` status.
- **AC-7:** A successfully created work item appears in the work-item list.
- **AC-8:** After successful creation, the creation form is reset to its initial state and the Create action becomes disabled again.
- **AC-9:** If creation fails, the entered form values remain available so that the user does not have to re-enter them.
- **AC-10:** If creation fails, the user is informed that the work item was not created.
- **AC-11:** Work-item creation rejects a request with a blank or whitespace-only title and does not create a work item.
- **AC-12:** Work-item creation rejects a request with an owner identifier that does not correspond to an existing owner, and does not create a work item.

---

## Scope Notes

- Owners are predefined for this increment; creating, editing, or deleting owners is out of scope.
- Owner assignment, work-item creation, status lifecycle, status history, description editing, and work-item edit/save behavior are the focus of this increment.
- Existing search and filtering behavior remains unchanged.
- Authentication, roles and permissions, comments, notifications, reporting dashboards, and other product features are out of scope.

---

## Future Backlog

- Add sortable columns to the work-item table.
- Add persistent left-side navigation.
- Add pagination with selectable items per page.
- Consider owner create/edit/delete functionality in a future increment.
- Standardize table row actions — Update the View and Delete buttons to follow the shared UX conventions and visual language used by other application actions, while preserving appropriate distinction between normal and destructive actions.
- API error-response cleanup — Invalid status values currently expose an internal Java enum/class name in the API error response. Return a client-facing validation error without exposing implementation details.
- Create-form submission race condition — A delayed work-item creation response can reset and re-disable the create form even after the user has already started entering a newer item, discarding that in-progress input. The work-item list refresh already guards against an equivalent stale-response hazard; creation does not. Apply the same protection to creation.
