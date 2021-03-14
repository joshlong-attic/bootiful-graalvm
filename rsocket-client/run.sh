#!/usr/bin/env bash
mvn -DskipTests=true clean spring-boot:build-image
docker run -e "SERVICE_HOST=tcp://host.docker.internal:8181" docker.io/library/rsocket-client:0.0.1-SNAPSHOT
