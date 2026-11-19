#!/bin/bash
# Usage: deploy.sh <path-to-war>
# Backs up the currently deployed WAR, then copies the new WAR to Tomcat.
set -euo pipefail

WAR="${1:?Usage: deploy.sh <war-file>}"
APP="feedback-portal"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/var/lib/tomcat9/webapps}"
BACKUP_DIR="${BACKUP_DIR:-/var/lib/jenkins/war-backups}"

[ -f "$WAR" ] || { echo "ERROR: WAR not found: $WAR"; exit 1; }
mkdir -p "$BACKUP_DIR"

if [ -f "$TOMCAT_WEBAPPS/$APP.war" ]; then
  TS=$(date +%Y%m%d-%H%M%S)
  cp "$TOMCAT_WEBAPPS/$APP.war" "$BACKUP_DIR/$APP-$TS.war"
  echo "Backed up current WAR to $BACKUP_DIR/$APP-$TS.war"
fi

echo "Deploying $WAR to $TOMCAT_WEBAPPS ..."
cp "$WAR" "$TOMCAT_WEBAPPS/$APP.war"
echo "Deploy copy complete. Tomcat will auto-explode the WAR."
