#!/usr/bin/env bash
set -e
DIRNAME=$(dirname ${BASH_SOURCE[0]})
if [ ! -f ${DIRNAME}/env.sh ]; then
    echo "${DIRNAME}/env.sh does not exist, please run ${DIRNAME}/setup.sh script before starting demo"
    exit 1
fi
source ${DIRNAME}/env.sh
source ${DIRNAME}/utils.sh


echo "Welcome to GigaSpaces schema evolution demo"
read -p "To start the demo, press enter to deploy v1 and v2 services:"
echo "This step might take a while..."
deploy_space "v1-service" "v1-service.jar"
deploy_space "v2-service" "v2-service.jar"
deploy_stateless_pu "v1-mirror" "v1-mirror.jar"
deploy_stateless_pu "v2-mirror" "v2-mirror.jar"
deploy_stateless_pu "v1-feeder" "feeder.jar"
echo -e "Services deployment is done, you can view the changes in https://${GS_MANAGER_IP}:8090/services"

read  -p "To run the next step - v2 load v1 db step, press enter: "
echo "Running v1 db load to v2 service..."
echo "Undeploying v1 mirror service"
undeploy_pu v1-mirror
echo "Deploying updated v1 mirror service, persistence to v1 mongodb will be paused"
deploy_stateless_pu "v1-mirror" "v1-temporary-mirror.jar"
echo "Deploying v2-load-v1-db service, v1 db will be written and adapted to v2"
deploy_stateless_pu "v2-load-v1-db" "v2-load-v1-db.jar"
echo -e "v2 load v1 db step is done, you can view the changes in https://${GS_MANAGER_IP}:8090/spaces"

read -p "To run the next step - v1 traffic redirection to v2, press enter: "
echo "Running v1 to v2 service traffic redirection ..."
echo "Undeploying v1 mirror service"
undeploy_pu v1-mirror
echo "Deploying updated v1 mirror service. Effects:"
echo "1. Persistence to v1 mongodb will resume."
echo "2. v1 traffic will be replicated and adapted to v2."
deploy_stateless_pu "v1-mirror" "v1-final-mirror.jar"
echo -e "All done, you can view the changes in https://${GS_MANAGER_IP}:8090/spaces"

