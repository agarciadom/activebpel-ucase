#!/bin/bash

# ActiveBPEL 5.5 launch script, rewritten to avoid jsvc and use sudo instead.
# Antonio García Domínguez <antonio.garciadominguez@uca.es>

########## CONFIGURATION #######################################################

TOMCAT_USER=`whoami`
TOMCAT_PORT=8080
TOMCAT_HOST=127.0.0.1

########## OTHER VARIABLES #####################################################

ACTIVEBPEL_HOME_URL=http://${TOMCAT_HOST}:${TOMCAT_PORT}/BpelAdmin/home.jsp
ACTIVEBPEL_ADMIN_URL=http://${TOMCAT_HOST}:${TOMCAT_PORT}/active-bpel/services/ActiveBpelAdmin
ACTIVEBPEL_START_URL=$ACTIVEBPEL_HOME_URL?action=start
ACTIVEBPEL_STOP_URL=$ACTIVEBPEL_HOME_URL?action=stop

########## FUNCTIONS ###########################################################

function check_env() {
    if ! type -P curl > /dev/null; then
        echo "Please install curl into somewhere in your PATH."
        return 1
    fi
    if ! type -P java > /dev/null; then
        echo "Please install Java (Sun JRE 5.0 is recommended)."
        echo "Make it accesible from some directory under your PATH"
        echo "environment variable."
        return 1
    fi
    if [ -z "$CATALINA_HOME" ]; then
        echo "Please set the CATALINA_HOME environment variable."
        return 1
    fi
    if [ ! -f "$CATALINA_HOME/webapps/BpelAdmin.war" ]; then
        echo "Please install ActiveBPEL into your Tomcat instance."
        return 1
    fi
}

function tomcat_running() {
    curl -so /dev/null http://"$TOMCAT_HOST":"$TOMCAT_PORT"
}

function activebpel_status() {
    STATUS=`curl -s "$ACTIVEBPEL_HOME_URL"\
        | sed -nre '/Status/,+1h;${g;s/.*>(.*)<.*/\1/p}'`

    if test -z "$STATUS"; then
      echo "Not running"
    else
      echo "$STATUS"
    fi
}

function activebpel_running() {
    STATUS=`activebpel_status`
    test "$STATUS" = "Running"
}

function activebpel_resume() {
    echo -n "Starting ActiveBPEL..."
    if ! curl -so /dev/null "$ACTIVEBPEL_START_URL"; then
        echo " error! check your logs."
        return 1
    fi
    echo " done."
}

function activebpel_pause() {
    echo -n "Stopping ActiveBPEL (use '$0 full-stop to stop Tomcat as well)..."
    if ! curl -so /dev/null "$ACTIVEBPEL_STOP_URL"; then
        echo " error! check your logs."
        return 1
    fi
    echo " done."
}

function start_tomcat() {
    if tomcat_running; then
        echo "Tomcat is already running"
        return 0
    else
        CATALINA_LOG=$CATALINA_HOME/logs/catalina.out
        JAVA_FLAGS="-server -Xmx500m"
        RDB_STATUS="OFF"

        # Enable remote debugging with the -debug option
        if test "$1" = "-debug"; then
          JAVA_FLAGS="$JAVA_FLAGS -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
          RDB_STATUS="ON"
        fi

        echo -n "Starting Tomcat (remote debugging $RDB_STATUS)..."
        sudo -u $TOMCAT_USER \
            sh -c "java $JAVA_FLAGS \
                   -Dcatalina.home=\"$CATALINA_HOME\"\
                   -Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl\
                   -jar \"$CATALINA_HOME\"/bin/bootstrap.jar \
                   2>&1 > $CATALINA_LOG" &
        until tomcat_running; do sleep 1s; done
        echo " done."
    fi
}

function stop_tomcat() {
    if ! tomcat_running; then
        echo "Tomcat is not running"
        return 0
    else
        echo -n "Stopping Tomcat..."
        sudo -u $TOMCAT_USER \
            java -Dcatalina.home="$CATALINA_HOME"\
                 -jar "$CATALINA_HOME"/bin/bootstrap.jar\
                 stop
        echo -n " message sent, waiting..."
        while tomcat_running; do sleep 0.1s; done
        if pgrep -f bootstrap.jar &>/dev/null; then
          echo -n " process still running: killing..."
          kill -9 `pgrep -f bootstrap.jar` || true
        fi
        echo " done."
    fi
}

function start_activebpel() {
    if ! tomcat_running; then
        start_tomcat $*
    elif activebpel_running; then
        echo "ActiveBPEL is already running."
        return 0
    else
        activebpel_resume
    fi

    # Sometimes the "start" message must be sent several times until
    # ActiveBPEL starts (it appears that when non-terminating mutants
    # are killed, an error is produced the first time ActiveBPEL is
    # asked to restart)
    echo -n "Waiting until ActiveBPEL is ready..."
    while true; do
        STATUS=`activebpel_status`

        if [ "$STATUS" = "Running" ]; then
            break;
        elif [ "$STATUS" = "Error" ]; then
            echo " retrying..."
            activebpel_resume
        fi

        sleep 0.2s
    done
    echo " done."
}

function stop_activebpel() {
    if ! tomcat_running; then
        echo "Cannot pause ActiveBPEL: Tomcat is not running."
    elif ! activebpel_running; then
        echo "Cannot pause ActiveBPEL: ActiveBPEL is not running."
    else
        activebpel_pause
    fi
}

function activebpel_list() {
    # 1. Get the process list for all running processes
    # 2. Filter <processId> elements (ignoring XML prefixes, if any)
    # 3. Remove everything except the numerical PIDs
    curl -s -H 'SOAPAction: ""' \
      "$ACTIVEBPEL_ADMIN_URL" \
      --data-binary @- \
      <<EOF | egrep -o "<[^>]*processId>([0-9]+)</[^>]+>" | tr -cd '0-9\n'
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
  xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:act="http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd">
   <soapenv:Header/>
   <soapenv:Body>
      <act:getProcessListInput>
         <act:filter>
            <act:processState>1</act:processState>
         </act:filter>
      </act:getProcessListInput>
   </soapenv:Body>
</soapenv:Envelope>
EOF

}

function activebpel_kill() {
    if [ "$#" != 1 ]; then
        echo "activebpel_kill requires the PID of the process to be killed"
        return 1
    fi
    PID=$1

    echo -n "Killing WS-BPEL process #$PID..."
    curl -s -o /dev/null\
         -H 'SOAPAction: ""' "$ACTIVEBPEL_ADMIN_URL" \
         --data-binary @- <<EOF
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
  xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:act="http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd">
   <soapenv:Header/>
   <soapenv:Body>
      <act:terminateProcessInput>
         <act:pid>$PID</act:pid>
      </act:terminateProcessInput>
   </soapenv:Body>
</soapenv:Envelope>
EOF
  if [ "$?" = 1 ]; then
      echo " error!"
  else
      echo " done."
  fi
}

function activebpel_kill_alt() {
    if [ "$#" != 1 ]; then
        echo "activebpel_kill requires the PID of the process to be killed"
        return 1
    fi
    PID=$1

    echo -n "Killing WS-BPEL process #$PID..."
    curl -s -o /dev/null\
         -H 'SOAPAction: ""' "$ACTIVEBPEL_ADMIN2_URL" \
         --data-binary @- <<EOF
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
  xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:act="http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd">
   <soapenv:Header/>
   <soapenv:Body>
     <act:aPid>$PID</act:aPid>
   </soapenv:Body>
</soapenv:Envelope>
EOF
  if [ "$?" = 1 ]; then
      echo " error!"
  else
      echo " done."
  fi
}

########## MAIN SCRIPT #########################################################

set -e
check_env

SUBCMD="$1"
shift

case "$SUBCMD" in
    start)
        start_activebpel $*
        ;;

    pause)
        stop_activebpel
        ;;

    restart)
        "$0" stop $* && "$0" start $*
        ;;

    stop)
        stop_tomcat
        ;;

    ls)
        activebpel_list
        ;;

    kill)
        activebpel_kill "$2"
        ;;

    killall)
        for i in `activebpel_list`; do
            activebpel_kill "$i"
        done
        ;;

    status)
        activebpel_status
        ;;

    *)
        echo "Usage: \"$0\" start [-debug]|pause|stop|restart|ls|kill|killall|status"
        exit 1
        ;;
esac
