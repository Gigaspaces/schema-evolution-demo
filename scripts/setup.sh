#!/usr/bin/env bash
set -e

DIRNAME=$(dirname ${BASH_SOURCE[0]})
if [ -f ${DIRNAME}/env.sh ]; then
    echo "${DIRNAME}/env.sh already exists, exiting"
    exit 0
fi
echo -e "#!/usr/bin/env bash" >> ${DIRNAME}/env.sh
echo -e "set -e" >> ${DIRNAME}/env.sh
echo "Welcome to demo setup"
read -rp "Enter your nomad token: "
echo -e "export GS_TOKEN=${REPLY}" >> ${DIRNAME}/env.sh
read -rp "Enter GigaSpaces Manager docker ip: "
echo -e "export GS_MANAGER_IP=${REPLY}" >> ${DIRNAME}/env.sh
chmod +x ${DIRNAME}/env.sh
echo "All set! to start, execute the demo-start.sh script"