#!/usr/bin/env bash
set -e
DIRNAME=$(dirname ${BASH_SOURCE[0]})
if [ ! -f ${DIRNAME}/env.sh ]; then
    echo "${DIRNAME}/env.sh does not exist, please run ${DIRNAME}/setup.sh script before starting demo"
    exit 1
fi
source ${DIRNAME}/env.sh
source ${DIRNAME}/utils.sh


echo -e "Stopping Schema Evolution demo, this might take a while..."
undeploy_all





