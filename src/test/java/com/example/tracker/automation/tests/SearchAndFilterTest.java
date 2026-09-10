package com.example.tracker.automation.tests;

import com.example.tracker.automation.BaseUiTest;
import com.example.tracker.automation.pageobjects.TrackerPage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class SearchAndFilterTest extends BaseUiTest {

    @Test
    void searchNarrowsTheListByTitle() {
        TrackerPage tracker = new TrackerPage(page).open(baseUrl);

        tracker.search("login timeout");

        assertThat(tracker.rowWithTitle("Investigate login timeout bug")).isVisible();
        assertThat(tracker.rowWithTitle("Set up staging environment")).hasCount(0);
    }

    @Test
    void filteringByStatusShowsOnlyMatchingItems() {
        TrackerPage tracker = new TrackerPage(page).open(baseUrl);

        tracker.filterByStatus("RESOLVED");

        assertThat(tracker.rowWithTitle("Verify data migration script")).isVisible();
        assertThat(tracker.rowWithTitle("Set up staging environment")).hasCount(0);
    }
}
