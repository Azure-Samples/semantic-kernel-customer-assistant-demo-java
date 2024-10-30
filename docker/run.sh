#!/bin/bash

if [ -f /run/secrets/demo ]; then
    mkdir -p /home/deploy/.sk || true
    cp /run/secrets/demo /home/deploy/.sk/conf.properties

    mkdir -p config || true
    cp /run/secrets/demo ./config/application.properties
fi

if [ -f /run/secrets/envFile ]; then
    cp /run/secrets/envFile ./.env
fi

java -jar quarkus-run.jar -Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8080
#java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 -jar quarkus-run.jar -Dquarkus.http.host=0.0.0.0
