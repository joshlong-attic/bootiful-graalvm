#!/usr/bin/env bash
docker run -p 8080 \
 -e "SPRING_CONFIG_IMPORT=configserver:" \
 -e "SERVER_PORT=8080" \
 -e "SPRING_APPLICATION_NAME=customers" \
 -e "SPRING_CLOUD_CONFIG_URI=http://host.docker.internal:8888" docker.io/library/config-client:0.0.1-SNAPSHOT
