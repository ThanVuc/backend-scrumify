#!/usr/bin/env bash
set -euo pipefail

COMMAND="${1:-}"

case "$COMMAND" in
  run)
    ./mvnw spring-boot:run
    ;;

  compile)
    ./mvnw compile
    ;;

  build)
    ./mvnw clean package
    ;;

  test)
    ./mvnw test
    ;;

  clean)
    ./mvnw clean
    ;;

  install)
    ./mvnw clean install
    ;;

  *)
    echo "Usage: bash mvnw.sh <command>"
    echo ""
    echo "Available commands:"
    echo "  run       Start Spring Boot application"
    echo "  compile   Compile source code"
    echo "  build     Build jar package"
    echo "  test      Run tests"
    echo "  clean     Clean target directory"
    echo "  install   Install package locally"
    exit 1
    ;;
esac