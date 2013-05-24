#!/bin/bash

# Script for updating the Dynamo .wsdl files from a running JBoss instance on port 8080.
# The JBoss instance should have been set up with the './install-jboss.sh' script.
#
# Copyright (C) 2013 Antonio García-Domínguez
# Licensed under the GPLv2

WSDL_CM_URL="http://localhost:8080/ConfigurationManagerBeanService/ConfigurationManagerBean?wsdl"
WSDL_CM_WSCOL=it.polimi.wscol/src/main/wsdl/configurationmanager.wsdl
WSDL_CM_DYN_WAR=it.polimi.dynamo.war/src/main/wsdl/ConfigurationManagerBean.wsdl

WSDL_ML_URL="http://localhost:8080/MonitorLoggerBeanService/MonitorLoggerBean?wsdl"
WSDL_ML_WSCOL=it.polimi.wscol/src/main/wsdl/monitorlogger.wsdl
WSDL_ML_DYN_WAR=it.polimi.dynamo.war/src/main/wsdl/MonitorLoggerBean.wsdl

wget -O- "$WSDL_CM_URL" > "$WSDL_CM_WSCOL"
wget -O- "$WSDL_CM_URL" > "$WSDL_CM_DYN_WAR"
wget -O- "$WSDL_ML_URL" > "$WSDL_ML_WSCOL"
wget -O- "$WSDL_ML_URL" > "$WSDL_ML_DYN_WAR"
