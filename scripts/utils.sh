#!/usr/bin/env bash
set -e

DIRNAME=$(dirname ${BASH_SOURCE[0]})
source ${DIRNAME}/env.sh
SERVICES=(v1-service v2-service v1-mirror v2-mirror v1-feeder)
function deploy_space {
  local puName="$1"
  local resource="$2"
  echo -e "Deploying service $puName..\n"
  local requestId=$(curl -X POST --insecure --silent --header 'Content-Type: application/json' --header 'Accept: text/plain' -u gs-admin:${GS_TOKEN} -d "{ \
     \"name\": \"${puName}\", \
     \"resource\": \"https://github.com/Gigaspaces/schema-evolution-demo/raw/master/pus/${resource}\", \
     \"topology\": { \
       \"schema\": \"partitioned\", \
       \"partitions\": 1, \
       \"backupsPerPartition\": 1 \
     }
   }" https://${GS_MANAGER_IP}:8090/v2/pus | jq .)
  assertRequest $requestId
  echo -e "Finished deployment of service $puName...\n"
}

function deploy_stateless_pu {
    local puName="$1"
    local resource="$2"
    echo -e "Deploying service $puName...\n"
    local requestId=$(curl -X POST --insecure --silent --header 'Content-Type: application/json' --header 'Accept: text/plain' -u gs-admin:${GS_TOKEN} -d "{ \
     \"name\": \"$puName\", \
     \"resource\": \"https://github.com/Gigaspaces/schema-evolution-demo/raw/master/pus/$resource\", \
     \"topology\": { \
       \"instances\": 1 \
     }
   }" https://${GS_MANAGER_IP}:8090/v2/pus | jq .)
    assertRequest $requestId
    echo -e "Finished deployment of service $puName...\n"
}

function undeploy_pu {
    local puName="$1"
    echo -e "Undeploying service $puName...\n"
    local requestId=$(curl -X DELETE --insecure --silent --header 'Accept: text/plain' -u gs-admin:${GS_TOKEN} https://${GS_MANAGER_IP}:8090/v2/pus/$puName | jq .)
    assertRequest $requestId
    echo -e "Finished undeployment of service $puName...\n"
}

function assertRequest {
  local requestId="$1"
  local requestStatus
  while [[ $requestStatus != \"successful\" ]]; do
    requestStatus=$(curl -X GET --insecure --silent --header 'Accept: text/plain' -u gs-admin:${GS_TOKEN} https://${GS_MANAGER_IP}:8090/v2/requests/$requestId | jq '.status')
    sleep 1
  done
#  if [[ $requestStatus == \"successful\" ]]; then
#    echo "Request $requestId was successful"
#  else
#    echo "Request $requestId failed"
#  fi
}

function undeploy_all {
    for service in "${SERVICES[@]}" ; do
        undeploy_pu $service
    done
}

function echo_green {
  local GREEN='\033[0;32m'
  local message="$1"
  echo -e "${GREEN}$message"
}

function echo_red {
  local GREEN='\033[0;31m'
  local message="$1"
  echo -e "${GREEN}$message"
}




