#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./scripts/start-pm2.sh [JAR_PATH] [CONFIG_PATH] [JAVA_OPTS]
#   ./scripts/start-pm2.sh [CONFIG_PATH] [JAVA_OPTS]             # backward compatible
# Examples:
#   ./scripts/start-pm2.sh /opt/dms/server.jar /opt/dms/application.yml "-Xms256m -Xmx512m"
#   ./scripts/start-pm2.sh /opt/dms/application.yml "-Xms256m -Xmx512m"   # old style
#   ./scripts/start-pm2.sh

ROOT_DIR="$(cd "$(dirname "$0")/" && pwd)"
ECOSYSTEM="$ROOT_DIR/pm2-ecosystem.config.cjs"

ARG1="${1:-}"
ARG2="${2:-}"
ARG3="${3:-}"

JAR_PATH=""
CONFIG_PATH=""
JAVA_OPTS_IN=""

# Arg parsing with backward compatibility
if [[ -n "${ARG1}" ]]; then
  if [[ -f "${ARG1}" && "${ARG1}" == *.jar ]]; then
    JAR_PATH="${ARG1}"
    CONFIG_PATH="${ARG2:-}"
    JAVA_OPTS_IN="${ARG3:-}"
  elif [[ "${ARG1}" == *.yml || "${ARG1}" == *.yaml ]]; then
    CONFIG_PATH="${ARG1}"
    JAVA_OPTS_IN="${ARG2:-}"
  else
    # if file exists but not .jar, treat as config
    if [[ -f "${ARG1}" ]]; then
      CONFIG_PATH="${ARG1}"
      JAVA_OPTS_IN="${ARG2:-}"
    fi
  fi
fi

if ! command -v pm2 >/dev/null 2>&1; then
  echo "[ERR] pm2 not found. Install with: npm i -g pm2" >&2
  exit 1
fi

mkdir -p "$ROOT_DIR/logs"

# Export env for pm2 ecosystem
if [[ -n "$JAR_PATH" ]]; then
  export JAR_PATH="$JAR_PATH"
  echo "[INFO] Using jar: $JAR_PATH"
fi
if [[ -n "$CONFIG_PATH" ]]; then
  export CONFIG_PATH="$CONFIG_PATH"
  echo "[INFO] Using external config: $CONFIG_PATH"
fi
if [[ -n "$JAVA_OPTS_IN" ]]; then
  export JAVA_OPTS="$JAVA_OPTS_IN"
  echo "[INFO] JAVA_OPTS: $JAVA_OPTS"
fi

# JAVA_BIN can be provided from env; pm2 config will honor it if set
if [[ -n "${JAVA_BIN:-}" ]]; then
  echo "[INFO] JAVA_BIN: $JAVA_BIN"
fi

# Start or reload
if pm2 describe dms-backend >/dev/null 2>&1; then
  pm2 reload "$ECOSYSTEM"
else
  pm2 start "$ECOSYSTEM"
fi

pm2 save
pm2 status dms-backend
