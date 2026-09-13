package com.example.tracker.service;

/**
 * Thrown when a request references an owner id that doesn't exist. Extends
 * IllegalArgumentException (rather than being an unrelated type) so callers that
 * only care about "bad input" broadly still catch it, while controllers that need
 * to tell it apart from a "work item not found" IllegalArgumentException (a 404,
 * not a 400) can catch this more specific type first.
 */
public class OwnerNotFoundException extends IllegalArgumentException {
    public OwnerNotFoundException(String message) {
        super(message);
    }
}
