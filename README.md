# End-to-End CI/CD Pipeline for a Java Web App (Git + Maven + Jenkins + Tomcat)

DevOps Internship Project. A "Student Feedback Portal" (Java Servlets, Maven WAR) is built, tested,
deployed and verified automatically by a Jenkins declarative pipeline, with a rollback path.

## Pipeline
`Checkout → Build → Test → Package → Archive → Deploy → Verify (curl /health) → auto-rollback on failure`

## Stack
Ubuntu, JDK 17, Maven, Git/GitHub, Jenkins (port 8080), Tomcat 9 (port 8081)

## Application
| URL | Purpose |
|---|---|
| `/feedback-portal/` | Home page |
| `/feedback-portal/feedback` | Form + list of feedback |
| `/feedback-portal/success` | Confirmation page |
| `/feedback-portal/health` | JSON health endpoint (used by pipeline) |

Dataset: `data/sample-feedback.csv` (25 rows), preloaded at startup.

## Run locally
```bash
mvn clean test
mvn package
cp target/feedback-portal.war /var/lib/tomcat9/webapps/
curl http://localhost:8081/feedback-portal/health
```

## Branching
`main` (stable, deployed) ← `dev` (integration) ← `feature/<name>`.
Commit style: `feat:`, `fix:`, `ci:`, `docs:`, `test:`.

## Docs
- [Setup guide](docs/setup-guide.md)
- [Architecture](docs/architecture.md)
- [Rollback runbook](docs/rollback-runbook.md)
- Screenshots go in `docs/screenshots/`
