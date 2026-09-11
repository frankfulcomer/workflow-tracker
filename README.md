# Workflow Tracker

[![CI](https://github.com/frankfulcomer/workflow-tracker/actions/workflows/ci.yml/badge.svg)](https://github.com/frankfulcomer/workflow-tracker/actions/workflows/ci.yml)

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
| UI automation  | Playwright (Java), JUnit 5, Page Object Model — lives in the companion [workflow-tracker-tests](https://github.com/frankfulcomer/workflow-tracker-tests) repo |
| CI             | GitHub Actions - builds this app on every push; the companion repo's own CI boots it and runs the Playwright suite against it |

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

## CI

Every push/PR to `main` builds this app via GitHub Actions
(`.github/workflows/ci.yml`) to catch build breaks early. The UI
automation suite itself runs in the companion
[workflow-tracker-tests](https://github.com/frankfulcomer/workflow-tracker-tests)
repo's own CI pipeline, which checks out this app, boots it, and runs the
Playwright suite against it on every push.

On a successful push to `main`, this pipeline also fires a
`repository_dispatch` event at that companion repo, carrying this
commit's SHA, so its regression suite runs against this exact revision
rather than waiting to be triggered on its own.

## What this is meant to demonstrate

- A real, working app to automate against — not a toy TODO list with no logic.
- A small but real **business rule** in `WorkItemService.updateStatus()`
  (an item can't skip straight from `NEW` to `CLOSED`) so the tests exercise
  actual behavior, not just "click button, thing appears."
- A companion **black-box** Playwright/JUnit suite, in its own repo with
  its own CI, using a Page Object Model and zero dependency on this app's
  source — the way it would actually be tested in a real environment.
- A DB-agnostic data layer (H2 for easy local/CI runs, Oracle profile
  for closer-to-production parity) using standard JPA/Hibernate.
- Two real CI pipelines, not just a badge for show: this repo builds the
  app, and the companion repo boots it and runs the black-box suite
  against it, on every push.

## Possible next steps

- API-level tests (REST assertions) alongside the UI tests, to show the
  test pyramid rather than only UI coverage.
- A `docker-compose.yml` with a real Oracle XE container for full parity.
- Basic auth / roles if the demo needs to show access-control testing.

<!-- verification commit: trivial change to exercise the cross-repo CI dispatch chain end-to-end -->
