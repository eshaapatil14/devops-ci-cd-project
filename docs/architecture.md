# Architecture

```
Developer --git push--> GitHub (main / dev / feature/*)
                           | webhook or SCM poll
                           v
                   Jenkins (8080)
   Checkout > Build > Test > Package > Archive > Deploy > Verify
                           |                         |
                           v                         v
              Archived WAR (artifact)      Tomcat 9 (8081) /var/lib/tomcat9/webapps
                                                     |
                                       http://<host>:8081/feedback-portal/
```

## SDLC mapping
Plan (README/issues) → Code (Git) → Build (Maven) → Test (JUnit) → Release (archived WAR) →
Deploy (Tomcat) → Monitor (health check, logs).

## Quality gates
- Tests fail → pipeline stops, no deploy.
- WAR missing → Package stage fails.
- Health check fails → automatic rollback to the previous backed-up WAR.

## Version safety
Every deploy backs up the previous WAR to `/var/lib/jenkins/war-backups`. Jenkins also archives each
build's WAR, and Git tags mark releases (`v1.0.0`).
