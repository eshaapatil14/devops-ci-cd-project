# Rollback & Failure Demo Runbook

## Case A: failing unit test (pipeline stops before deploy)
1. In `FeedbackStoreTest`, change an expected value (e.g. `assertEquals(1, s.count())` to `2`).
2. `git commit -am "test: intentionally break test"` and push.
3. Expected: Test stage FAILS, Package/Deploy/Verify are skipped. Screenshot the red build + console log.
4. Fix the test, push, pipeline goes green.

## Case B: bad deploy, restore previous WAR
Automatic: if `/health` fails, the Verify stage runs `scripts/rollback.sh` and re-checks health.
To demo: make `HealthServlet` return 500 (or `VERSION` mismatch), push to `main`, watch Verify fail and rollback.

Manual:
```bash
ls -lt /var/lib/jenkins/war-backups/
sudo -u jenkins scripts/rollback.sh                     # newest backup
sudo -u jenkins scripts/rollback.sh /var/lib/jenkins/war-backups/feedback-portal-<timestamp>.war
scripts/health-check.sh http://localhost:8081/feedback-portal
```
Alternative: download an older WAR from the Jenkins build's *Artifacts* and copy it to Tomcat `webapps/`.

## Case C: Git rollback
```bash
git log --oneline
git revert <bad-commit-sha>
git push origin main        # pipeline re-runs and redeploys the good code
```
Tagging: `git tag -a v1.0.0 -m "first stable release" && git push --tags`

## Evidence checklist
Screenshots of: successful build, failed test build, archived WAR, Tomcat page, health JSON,
rollback console output. Save under `docs/screenshots/`.
