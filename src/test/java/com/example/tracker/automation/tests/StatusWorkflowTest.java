package com.example.tracker.automation.tests;

import com.example.tracker.automation.BaseUiTest;
import com.example.tracker.automation.pageobjects.TrackerPage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class StatusWorkflowTest extends BaseUiTest {

    @Test
    void movesAnItemThroughTheHappyPathWorkflow() {
        TrackerPage tracker = new TrackerPage(page).open(baseUrl);

        String title = "Automated test - happy path workflow";
        tracker.createItem(title, "Frank", "Should move NEW -> IN_PROGRESS -> RESOLVED -> CLOSED");
        assertThat(tracker.rowWithTitle(title)).isVisible();

        tracker.setStatusForRow(title, "IN_PROGRESS");
        assertThat(tracker.rowWithTitle(title).locator(".badge")).hasText("IN PROGRESS");

        tracker.setStatusForRow(title, "RESOLVED");
        assertThat(tracker.rowWithTitle(title).locator(".badge")).hasText("RESOLVED");

        tracker.setStatusForRow(title, "CLOSED");
        assertThat(tracker.rowWithTitle(title).locator(".badge")).hasText("CLOSED");
    }

    @Test
    void deletingAnItemRemovesItFromTheList() {
        TrackerPage tracker = new TrackerPage(page).open(baseUrl);

        String title = "Automated test - to be deleted";
        tracker.createItem(title, "Frank", "This one gets removed");
        assertThat(tracker.rowWithTitle(title)).isVisible();

        tracker.deleteRow(title);

        assertThat(tracker.rowWithTitle(title)).hasCount(0);
    }
}
