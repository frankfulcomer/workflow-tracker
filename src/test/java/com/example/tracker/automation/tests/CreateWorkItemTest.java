package com.example.tracker.automation.tests;

import com.example.tracker.automation.BaseUiTest;
import com.example.tracker.automation.pageobjects.TrackerPage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class CreateWorkItemTest extends BaseUiTest {

    @Test
    void createsANewItemAndDisplaysItInTheTable() {
        TrackerPage tracker = new TrackerPage(page).open(baseUrl);

        String title = "Automated test - verify export button";
        tracker.createItem(title, "Frank", "Created by a Playwright UI test");

        // assertThat(...) here is Playwright's web-first assertion: it retries
        // until the element appears (or a timeout is hit) instead of failing
        // on the first check, which is what makes these tests resist flake.
        assertThat(tracker.rowWithTitle(title)).isVisible();
    }

    @Test
    void rejectsAnItemWithNoTitle() {
        TrackerPage tracker = new TrackerPage(page).open(baseUrl);

        tracker.createItem("", "Frank", "Missing a title on purpose");

        // The HTML5 "required" attribute blocks submission client-side,
        // so the row should never appear and no request should succeed.
        assertThat(tracker.rowWithTitle("Missing a title on purpose")).hasCount(0);
    }
}
