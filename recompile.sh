#!/bin/bash

CATALINA_USER=`whoami` # Tomcat user
CATALINA_GROUP=`whoami` # Tomcat group

# Configuration file which should be backed up and restored, if existing
CONF_FILE=${CATALINA_HOME}/bpr/aeEngineConfig.xml
# Configuration file to be used the first time
FIRST_CONF_FILE=aeEngineConfig.xml
# Directory where libraries should be copied to
LIB_DIR=${CATALINA_HOME}/shared/lib

# Commands to be run to start and stop the server
CMD_START_TOMCAT="ActiveBPEL.sh start"
CMD_STOP_TOMCAT="ActiveBPEL.sh full-stop"

### END CONFIGURATION

# Stops ActiveBPEL
${CMD_STOP_TOMCAT}

# Compiles ActiveBPEL
{
  pushd projects/support;
  ant -f activebpel.xml activebpel.all;
  popd;
}

# Backups old conf and installs the new ActiveBPEL
TMPCONF_FILE=""
if [ -f "${CONF_FILE}" ]; then
    TMPCONF_FILE=`sudo -u "${CATALINA_USER}" mktemp`
    sudo -u "${CATALINA_USER}" cp "${CONF_FILE}" "${TMPCONF_FILE}"
fi
sudo -E true
if [ "$?" = "0" ]; then
  sudo -E -u "${CATALINA_USER}" ./install.sh
else
  sudo -u "${CATALINA_USER}" ./install.sh
fi

# Restores the backup or uses the default configuration instead
if [ -f "${TMPCONF_FILE}" ]; then
    sudo -u "${CATALINA_USER}" cp "${TMPCONF_FILE}" "${CONF_FILE}";
else
    sudo -u "${CATALINA_USER}" cp "${FIRST_CONF_FILE}" "${CONF_FILE}";
fi

# Installs the XPath logging extensions
sudo -u "${CATALINA_USER}" cp *.jar "${LIB_DIR}"

# Sets the proper permissions
sudo chown -R ${CATALINA_USER}.${CATALINA_GROUP} ${CATALINA_HOME}

# Starts up Tomcat (and ActiveBPEL)
${CMD_START_TOMCAT}
