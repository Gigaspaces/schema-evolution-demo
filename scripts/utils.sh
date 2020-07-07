#!/usr/bin/env bash
set -e

DIRNAME=$(dirname ${BASH_SOURCE[0]})
source ${DIRNAME}/env.sh
SERVICES=(v1-service v2-service v1-mirror v2-mirror v1-feeder v2-load-v1-db)
function deploy_space {
  local puName="$1"
  local resource="$2"
  echo "Deploying service $puName.."
  local requestId=$(curl -X POST --insecure --silent --header 'Content-Type: application/json' --header 'Accept: text/plain' -u adam:${GS_TOKEN} -d "{ \
     \"name\": \"${puName}\", \
     \"resource\": \"https://github.com/alonshoham/schema-evolution-demo/raw/master/pus/${resource}\", \
     \"topology\": { \
       \"schema\": \"partitioned\", \
       \"partitions\": 1, \
       \"backupsPerPartition\": 1 \
     }
   }" https://${GS_MANAGER_IP}:8090/v2/pus | jq .)
  assertRequest $requestId
}

function deploy_stateless_pu {
    local puName="$1"
    local resource="$2"
    echo "Deploying service $puName.."
    local requestId=$(curl -X POST --insecure --silent --header 'Content-Type: application/json' --header 'Accept: text/plain' -u adam:${GS_TOKEN} -d "{ \
     \"name\": \"$puName\", \
     \"resource\": \"https://github.com/alonshoham/schema-evolution-demo/raw/master/pus/$resource\", \
     \"topology\": { \
       \"instances\": 1 \
     }
   }" https://${GS_MANAGER_IP}:8090/v2/pus | jq .)
    echo "requestId: $requestId"
    assertRequest $requestId
}

function undeploy_pu {
    local puName="$1"
    local requestId=$(curl -X DELETE --insecure --silent --header 'Accept: text/plain' -u adam:${GS_TOKEN} https://${GS_MANAGER_IP}:8090/v2/pus/$puName | jq .)
    echo "requestId: $requestId"
    assertRequest $requestId
}

function assertRequest {
  local requestId="$1"
  local requestStatus
  local count=0
  echo "asserting requestId $requestId"
  while [[ $requestStatus != \"successful\" ]]; do
    requestStatus=$(curl -X GET --insecure --silent --header 'Accept: text/plain' -u adam:${GS_TOKEN} https://${GS_MANAGER_IP}:8090/v2/requests/$requestId | jq '.status')
    sleep 1
    count=$[$count+1]
  done
  if [[ $requestStatus == \"successful\" ]]; then
    echo "Request $requestId was successful"
  else
    echo "Request $requestId failed"
  fi
}

function undeploy_all {
    for service in "${SERVICES[@]}" ; do
        undeploy_pu $service
    done
}




