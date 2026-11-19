#!/bin/bash
# Usage: health-check.sh <base-url>   e.g. http://localhost:8081/feedback-portal
# Retries because Tomcat needs a few seconds to explode and start the WAR.
set -uo pipefail

BASE="${1:?Usage: health-check.sh <base-url>}"
RETRIES="${RETRIES:-12}"
SLEEP="${SLEEP:-5}"

for i in $(seq 1 "$RETRIES"); do
  CODE=$(curl -s -o /tmp/health.out -w "%{http_code}" "$BASE/health" || echo "000")
  echo "Attempt $i/$RETRIES -> HTTP $CODE"
  if [ "$CODE" = "200" ] && grep -q '"status":"UP"' /tmp/health.out; then
    echo "Health check PASSED: $(cat /tmp/health.out)"
    exit 0
  fi
  sleep "$SLEEP"
done

echo "Health check FAILED after $RETRIES attempts"
exit 1
