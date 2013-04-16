#!/bin/bash

set +x

main_jar() {
    PROJECT=$1
    ls "$PROJECT"/target/*.jar | egrep -v 'sources|javadoc'
}

if [ -z "$JBOSS_DIR" ]; then
    echo "Please set JBOSS_DIR to your JBoss 4.2 installation directory." >&2
    exit 1
fi

HV_P=it.polimi.historicalvars
CM_P=it.polimi.monitor.configurationmanager
ML_P=it.polimi.monitor.monitorlogger
DEMO_JARS=dynamo-demo/jboss

SERVER_DEPLOY="$JBOSS_DIR/server/default/deploy/"

# Build all JBoss jars and their dependencies
mvn -am -pl "$HV_P,$CM_P,$ML_P" clean install

# Copy them over to the JBoss directory
cp -v "$(main_jar "$HV_P")" "$SERVER_DEPLOY"/historicalVariable.jar
cp -v "$(main_jar "$CM_P")" "$SERVER_DEPLOY"/configurationManager.jar
cp -v "$(main_jar "$ML_P")" "$SERVER_DEPLOY"/monitorLogger.jar
cp -v "$DEMO_JARS"/*.jar "$SERVER_DEPLOY"
