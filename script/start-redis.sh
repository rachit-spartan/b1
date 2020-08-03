#!/bin/sh
docker run -p 6379:6379 --name redis -e ALLOW_EMPTY_PASSWORD=yes bitnami/redis:latest