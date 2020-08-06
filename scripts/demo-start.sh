#!/usr/bin/env bash
set -e
DIRNAME=$(dirname ${BASH_SOURCE[0]})
if [ ! -f ${DIRNAME}/env.sh ]; then
    echo "${DIRNAME}/env.sh does not exist, please run ${DIRNAME}/setup.sh script before starting demo"
    exit 1
fi
source ${DIRNAME}/env.sh
source ${DIRNAME}/utils.sh


echo_green "\nWelcome to GigaSpaces schema evolution demo\n
This demo is run on GigaSpaces ElasticGrid\n
It shows the full transition between a running service to a new service, containing an updated schema\n"

read -rp "To start the demo, press enter: "
echo_green "\n************************************************\n
The first step is deployment of the following services:\n
  1. v1 service and mirror\n
  2. v2 service and mirror\n
  3. v1 feeder, that writes old schema data to v1\n"
read -rp "To start services deployment, press enter: "
echo_green "\nThis step might take a while...\n"
deploy_space "v1-service" "v1-service.jar"
deploy_space "v2-service" "v2-service.jar"
deploy_stateless_pu "v1-mirror" "v1-mirror.jar"
deploy_stateless_pu "v2-mirror" "v2-mirror.jar"
deploy_stateless_pu "v1-feeder" "feeder.jar"
echo_green "************************************************\n
Services deployment is done, in this stage the following happened:\n
  - v1 service is running.\n
  - v1 service is constantly fed with data, which is also persisted to kafka and to mongodb database.\n
  - v2 service is running and idle for now.\n
you can view all processes in https://${GS_MANAGER_IP}:8090/services\n"

read  -rp "To move to the next step, press enter: "
echo_green "\n************************************************\n
The next step in the demo is loading data stored in v1 database to the v2 service\n
  - The v1 mirror is redeployed, resulting in a temporary pause of writes to v1 database\n
  - A stateless service called v2-load-v1-db is deployed. This service executes load of v1 database to v2.\n
NOTICE: the data written to v2 is adapted to the new v2 schema:\n
  - Field typeChangeField type is changed from String to Integer.\n
  - Fields newField and calculatedField are added\n"

read  -rp "To run this step, press enter: "
echo_green "\nRunning v1 db load to v2...\n"
undeploy_pu "v1-mirror"
deploy_stateless_pu "v1-mirror" "v1-temporary-mirror.jar"
echo_green "Deployed the new v1 mirror service, persistence to v1 mongodb database will be paused.\n"
deploy_stateless_pu "v2-load-v1-db" "v2-load-v1-db.jar"
echo_green "Deployed the v2-load-v1-db v1 database will be written and adapted to v2.\n"
echo_green "v2 load v1 db step is done, you can view the changes in https://${GS_MANAGER_IP}:8090/spaces\n"
read  -rp "To move to the next step, press enter: "
echo_green "\n************************************************\n
The next and final step in th demo is redirection of v1 traffic to v2\n
  - The final version of v1 mirror is deployed\n
  - All v1 traffic (i.e. written data) is replicated and adapted to v2\n
  - This v1 mirror is responsible for this functionality.\n"

read -rp "To run the next step - v1 traffic redirection to v2, press enter: "
echo_green "\nRunning v1 to v2 service traffic redirection ...\n"
undeploy_pu "v1-mirror"
deploy_stateless_pu "v1-mirror" "v1-final-mirror.jar"
echo_green "Final step is done, you can view the changes in https://${GS_MANAGER_IP}:8090/spaces\n"
read -rp "To exit the demo, press enter: "

