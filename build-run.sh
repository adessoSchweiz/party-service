#!/usr/bin/env bash

cd $(dirname $0)

VERSION=1.0.0
echo VERSION: $VERSION

mvn clean install
docker build -t adesso/party-service:${VERSION} .
docker stop party-service
docker rm party-service
docker run -d --name party-service --net=hackathon -p 8091:8080 adesso/party-service:${VERSION}
docker logs party-service -f
