#!/usr/bin/env bash
mvn clean spring-boot:build-image
docker run -p 8080:8080 -e SERVER_PORT=8080 docker.io/library/bootiful:0.0.1-SNAPSHOT
