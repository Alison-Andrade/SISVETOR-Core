#!/usr/bin/env bash
set -e

exec 3<>/dev/tcp/127.0.0.1/8080
printf 'GET /api/v1/actuator/health HTTP/1.0\r\nHost: localhost\r\n\r\n' >&3
IFS= read -r status <&3
[[ "$status" == "HTTP/1.1 200 "* || "$status" == "HTTP/1.0 200 "* ]]
