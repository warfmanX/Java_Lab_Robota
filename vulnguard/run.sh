#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

usage() {
  cat <<-EOF
Usage: $(basename "$0") <command>

Commands:
  docker-start    Build and start with Docker Compose (detached)
  docker-logs     Tail application logs
  docker-down     Stop and remove compose services
  local-build     Run 'mvn clean package'
  local-start     Build and run locally (foreground)
  status          Show docker compose ps
  help            Show this help
EOF
}

check_port() {
  if command -v lsof >/dev/null 2>&1; then
    if lsof -iTCP:8080 -sTCP:LISTEN -Pn >/dev/null 2>&1; then
      echo "Port 8080 is in use. Stop the process that binds it or use Docker with --no-deps app." >&2
      return 1
    fi
  fi
  return 0
}

case ${1:-help} in
  docker-start)
    docker compose up --build -d
    ;;
  docker-logs)
    docker compose logs --follow app
    ;;
  docker-down)
    docker compose down
    ;;
  local-build)
    mvn clean package
    ;;
  local-start)
    check_port || exit 1
    mvn spring-boot:run
    ;;
  status)
    docker compose ps
    ;;
  help|--help|-h)
    usage
    ;;
  *)
    echo "Unknown command: ${1:-}" >&2
    usage
    exit 2
    ;;
esac
