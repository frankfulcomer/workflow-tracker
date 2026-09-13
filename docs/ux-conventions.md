# Workflow Tracker — Shared UX Conventions

These conventions define common user-interface behavior and presentation for Workflow Tracker.

They apply across the application unless a product story or acceptance criterion explicitly defines different behavior.

## Form Layout

- Form fields within the same view are aligned consistently.
- Related fields use consistent spacing.
- Labels and their associated controls are presented consistently.
- Textareas use an intentionally defined size and resize behavior rather than browser-default resizing.

## Action Areas

- Form actions are grouped in a clearly identifiable action area.
- Action groups are aligned to the bottom-right of their form or modal.
- When primary and secondary actions appear together, the primary action appears on the far right.
- Primary and secondary actions remain visually distinguishable.
- Disabled actions are visually distinguishable from enabled actions.
- Buttons within the same action group use consistent height and padding while retaining distinct primary and secondary visual treatment.

## Interaction Consistency

- Similar actions behave consistently across views.
- Actions that commit user-entered data are presented as primary actions.
- Actions that dismiss or abandon a view are presented as secondary actions.
- User-entered data should not be discarded without confirmation when the application knows unsaved changes are present.

## Scope

These conventions establish shared UX expectations without prescribing specific CSS values, colors, dimensions, or implementation techniques.

Story-specific behavior takes precedence when explicitly defined by a product story or acceptance criterion.