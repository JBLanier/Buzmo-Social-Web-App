#!/bin/bash

mvn install:install-file -Dfile=src/main/resources/ojdbc6.jar -DgroupId=oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar
