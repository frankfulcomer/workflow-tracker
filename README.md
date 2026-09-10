# Workflow Tracker

A small, self-contained web app for tracking work items through a status
pipeline (`NEW -> IN_PROGRESS -> RESOLVED -> CLOSED`), built as a **test
automation demo**: real app, real UI automation suite, real CI pipeline.

It's intentionally generic (a "ticket/request tracker") rather than tied to
any one company's domain, so it doubles as a general-purpose QA portfolio
piece.

## Stack

| Layer          | Choice                                   |
|----------------|-------------------------------------------|
| Backend        | Java 17, Spring Boot 3, Spring Data JPA   |
| Database       | H2 (in-memory, zero setup) by default; Oracle XE via a Spring profile |
| Frontend       | Plain HTML / CSS / vanilla JS (no framework, no build step) |
| UI automation  | Playwright (Java), JUnit 5, Page Object Model |
| CI             | GitHub Actions - runs the full Playwright suite headless on every push |

## Running it locally

Requires Java 17+ and Maven.

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080**. The app seeds a handful of demo
work items on first startup so it's never an empty screen.

The H2 console (useful for showing the data live in a demo) is at
**http://localhost:8080/h2-console** — JDBC URL `jdbc:h2:mem:trackerdb`,
user `sa`, no password.

### Running against real Oracle instead

The app is deliberately not hardcoded to H2. To point it at a real Oracle
database (e.g. Oracle XE running locally):

```bash
export ORACLE_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
export ORACLE_USER=tracker
export ORACLE_PASSWORD=yourpassword

mvn spring-boot:run -Dspring-boot.run.profiles=oracle
```

No code changes needed — Spring picks up `application-oracle.properties`
and Hibernate creates the schema on first run.

## Running the automation suite

First time only, install the Playwright browser binaries:

```bash
mvn exec:java -e -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --with-deps chromium"
```

Then:

```bash
mvn test
```

This boots the real Spring Boot application on a random port (no mocks)
and drives it with headless Chromium through Playwright — the same way a
QA engineer would test a deployed environment.

Tests are organized as:

```
src/test/java/com/example/tracker/automation/
  BaseUiTest.java              # boots the app + browser for every test
  pageobjects/TrackerPage.java # locators + interactions, one place to update on UI changes
  tests/
    CreateWorkItemTest.java
    StatusWorkflowTest.java
    SearchAndFilterTest.java
```

## CI

Every push/PR to `main` runs the full suite headless via GitHub Actions
(`.github/workflows/ci.yml`) and uploads the test report as a build
artifact.

## What this is meant to demonstrate

- A real, working app to automate against — not a toy TODO list with no logic.
- A small but real **business rule** in `WorkItemService.updateStatus()`
  (an item can't skip straight from `NEW` to `CLOSED`) so the tests exercise
  actual behavior, not just "click button, thing appears."
- A **Page Object Model** so tests stay readable and a UI change only
  requires updating one file.
- Tests that treat the app as a black box (real HTTP, real browser),
  the way it would actually be tested in an environment.
- A DB-agnostic data layer (H2 for easy local/CI runs, Oracle profile
  for closer-to-production parity) using standard JPA/Hibernate.
- A CI pipeline that actually runs the suite, not just a badge for show.

## Possible next steps

- API-level tests (REST assertions) alongside the UI tests, to show the
  test pyramid rather than only UI coverage.
- A `docker-compose.yml` with a real Oracle XE container for full parity.
- Basic auth / roles if the demo needs to show access-control testing.
