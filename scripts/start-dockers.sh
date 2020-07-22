#!/usr/bin/env bash
set -e

docker run -d --name kafka --network host -e KAFKA_CFG_ZOOKEEPER_CONNECT=127.0.1.1:2181 -e KAFKA_CFG_BROKER_ID=1 -e ALLOW_PLAINTEXT_LISTENER=yes bitnami/kafka:2.5.0
docker run --net=host --name mongo -d mongo:3.6.18-xenial
docker run --net=host -d --rm -e KAFKA_BROKERCONNECT=127.0.1.1:9092 --name kafdrop obsidiandynamics/kafdrop:latest