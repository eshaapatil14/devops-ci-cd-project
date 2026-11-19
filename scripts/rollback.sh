#!/bin/bash
# Restores the most recent backed-up WAR (or a specific one passed as $1).
set -euo pipefail

APP="feedback-portal"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/var/lib/tomcat9/webapps}"
BACKUP_DIR="${BACKUP_DIR:-/var/lib/jenkins/war-backups}"

TARGET="${1:-$(ls -1t "$BACKUP_DIR"/$APP-*.war 2>/dev/null | head -n1 || true)}"
[ -n "$TARGET" ] && [ -f "$TARGET" ] || { echo "ERROR: no backup WAR available to roll back to"; exit 1; }

echo "Rolling back to: $TARGET"
cp "$TARGET" "$TOMCAT_WEBAPPS/$APP.war"
echo "Rollback WAR copied. Tomcat will redeploy it."
