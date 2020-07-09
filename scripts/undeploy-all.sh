#!/usr/bin/env bash
set -e

docker rm -f $(docker ps -aq)