package com.example.tracker.automation.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object for the main tracker screen. Tests talk to this class, never
 * to raw locators - a UI change only requires updating this one file.
 *
 * Playwright's locators are "lazy" and auto-wait for the element to be
 * present and actionable before acting, so (unlike the Selenium version of
 * this file) there's no need for manual WebDriverWait / polling loops.
 */
public class TrackerPage {

    private final Page page;

    public TrackerPage(Page page) {
        this.page = page;
    }

    public TrackerPage open(String baseUrl) {
        page.navigate(baseUrl + "/index.html");
        page.locator("#title").waitFor();
        return this;
    }

    public TrackerPage createItem(String title, String assignee, String description) {
        page.fill("#title", title);
        page.fill("#assignee", assignee);
        page.fill("#description", description);
        page.click("#create-form button[type='submit']");
        return this;
    }

    public String createErrorText() {
        return page.locator("#create-error").innerText();
    }

    public TrackerPage search(String text) {
        page.fill("#search-box", text);
        return this;
    }

    public TrackerPage filterByStatus(String status) {
        page.selectOption("#status-filter", status);
        return this;
    }

    /** Row locator scoped by the item's title text - waits automatically when asserted on. */
    public Locator rowWithTitle(String title) {
        return page.locator("#items-body tr", new Page.LocatorOptions().setHasText(title));
    }

    public void setStatusForRow(String title, String status) {
        rowWithTitle(title).locator("select").selectOption(status);
    }

    public String statusBadgeText(String title) {
        return rowWithTitle(title).locator(".badge").innerText();
    }

    public void deleteRow(String title) {
        rowWithTitle(title).getByRole(AriaRole.BUTTON).click();
    }

    public int rowCount() {
        return page.locator("#items-body tr").count();
    }
}
