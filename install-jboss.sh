#!/bin/bash

set -e

### Configuration

# Path under which all downloads should be cached to save bandwidth
DL_CACHE="${HOME}/Downloads"

# Download URL for the JBoss 4.2 distribution
JBOSS42_URL="http://hivelocity.dl.sourceforge.net/project/jboss/JBoss/JBoss-4.2.3.GA/jboss-4.2.3.GA-jdk6.zip"

# Default deploy directory
SERVER_DEPLOY="$JBOSS_DIR/server/default/deploy/"

### Utility functions

function echo_err() {
    echo "$@" >&2
}

function log() {
    if [[ "$#" < 2 ]]; then
        error "Wrong parameters: expected level and rest of message, got $@"
        return 1
    fi
    LEVEL="$1"; shift
    echo_err "`date -R`: ($LEVEL) $@"
}

function status() {
    log INFO "$@"
}

function error() {
    log ERROR "$@"
}

function push_d() {
    # Create if it doesn't exist
    if test -f "$1"; then
        error "$1 is a file"
        return 1
    fi
    mkdir -p "$1"
    pushd "$1" > /dev/null
}

function pop_d() {
    popd > /dev/null
}

function confirm() {
    RESULT=""
    while test "$RESULT" != "y" -a "$RESULT" != "n" ; do
        echo_err -n "$1 [yn] "
        read RESULT
        RESULT=`tr [:upper:] [:lower:] <<<"$RESULT"`
    done
    test "$RESULT" = "y"
}

function confirm_rm() {
    if test "$#" == 0; then
        error "Usage: [-s] path(s)..."
        error "   -s: use root privileges (sudo) for removing the paths"
        exit 1
    fi

    # -s: use sudo to run rm
    OPTIND=1
    MSG_TEXT="All files under the following paths will be removed with the rights of user"
    if getopts :s OPCIONES; then
        MSG_TEXT="$MSG_TEXT 'root':"
        CMD_PREFIX="sudo "
    else
        MSG_TEXT="$MSG_TEXT '`whoami`':"
        CMD_PREFIX=""
    fi

    SOME_FILE_EXISTS=0
    for f in $@; do
        if test -e "$f"; then
            if test "$SOME_FILE_EXISTS" == 0; then
                echo "$MSG_TEXT"
                SOME_FILE_EXISTS=1
            fi
            echo "  - $f"
        fi
    done
    if test "$SOME_FILE_EXISTS" == 1; then
        if confirm "Are you sure?"; then
            $CMD_PREFIX rm -rf -- $@
        else
            return 1
        fi
    fi
}

function download_from_http() {
    if [ "$#" != 1 ]; then
        error "Usage: download_http (URL)"
        return 1
    fi

    URL=$1
    DEST_PATH=$DL_CACHE/$(basename "$URL")

    if test -f "$DEST_PATH"; then
        status "File already available at $DEST_PATH: reusing"
    else
        push_d "$DL_CACHE"
        status "Downloading $URL..."
        if ! wget --no-check-certificate --no-http-keep-alive --timestamping "$URL"; then
            error "Failed to download $URL"
            return 2
        fi
        status "File downloaded to $DEST_PATH"
        pop_d
    fi

    echo $DEST_PATH
}

function unpack() {
  if test "$#" == 0 || test "$#" -gt 2; then
    error "Usage: unpack [-np] (archive)"
    error "   -n: do not recreate directories (only for .zip/.jar)"
    error "   -p: print main directory basename after unpacking (conflicts with -n)"
    return 1
  fi

  status "Unpacking with $* at $(pwd)..."
  unset JUNK_PATHS
  unset PRINT_MAIN_DIR
  OPTIND=1
  while getopts :np OPTION; do
      case "$OPTION" in
          n) JUNK_PATHS="yes"; status "Will not recreate paths";;
          p) PRINT_MAIN_DIR="yes"; status "Will print the main directory";;
      esac
  done
  shift $(( $OPTIND - 1 ))
  ARCHIVE="$1"

  if [ -n "$JUNK_PATHS" ]; then
      case "$ARCHIVE" in
          *.tar.*|*.t*)
              error "Cannot discard paths in the $ARCHIVE tar file! Bailing out..."
              return 3
              ;;
      esac
  elif ! test -e "$ARCHIVE"; then
      error "Cannot unpack $ARCHIVE: does not exist"
      return 4
  elif ! test -f "$ARCHIVE"; then
      error "Cannot unpack $ARCHIVE: it's not a file"
      return 5
  fi

  case "$ARCHIVE" in
    *.tar.gz|*.tgz)
          MAIN_DIR=$(tar xvzf "$ARCHIVE" | tail -1 | awk -F '/' '{print $1}')
          ;;
    *.tar.bz2)
          MAIN_DIR=$(tar xvjf "$ARCHIVE" | tail -1 | awk -F '/' '{print $1}')
          ;;
    *.zip|*.jar)
          unset FLAGS
          if [ -n "$JUNK_PATHS" ]; then
              FLAGS="-j"
          fi
          MAIN_DIR=$(unzip -v "$ARCHIVE" | grep -w 00000000 | tail -1 | awk '{print $8}' | awk -F/ '{print $1}')
          unzip -qq $FLAGS "$ARCHIVE"
          ;;
    *)
      error "Unknown file extension in $ARCHIVE"
      return 6
      ;;
  esac

  if [ -n "$PRINT_MAIN_DIR" ]; then
      status "Main directory of $ARCHIVE: $MAIN_DIR"
      echo "$MAIN_DIR"
  fi
}

### Main body

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

# Build all JBoss jars and their dependencies
mvn -am -pl "$HV_P,$CM_P,$ML_P" install

# Reinstall JBoss
JBOSS42_ZIP=$(download_from_http "$JBOSS42_URL")
if ! confirm_rm "$JBOSS_DIR"; then
    return 1
fi
mv "$(unpack -p "$JBOSS42_ZIP")" "$JBOSS_DIR"

# Pack the dynamo-war directory
pushd "$DEMO_JARS"/dynamo-war
zip -r ../dynamo.war *
popd

# Copy the Dynamo .jar files over to the JBoss directory
cp -v "$(main_jar "$HV_P")" "$SERVER_DEPLOY"/HistoricalVariableBeanService.jar
cp -v "$(main_jar "$CM_P")" "$SERVER_DEPLOY"/ConfigurationManagerBeanService.jar
cp -v "$(main_jar "$ML_P")" "$SERVER_DEPLOY"/MonitorLogger.jar
cp -v "$DEMO_JARS"/*.{jar,war} "$SERVER_DEPLOY"
