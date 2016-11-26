#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd ${DIR}

${DIR}/src/main/resources/frontend/build.sh

mvn clean package
