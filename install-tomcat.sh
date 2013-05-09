#!/bin/bash

# Exit on error
set -e
# Use error code from last command in a pipe chain
set -o pipefail

### Configuration

# Path under which all downloads should be cached to save bandwidth
DL_CACHE="${HOME}/Downloads"

# Host which serves Nexus and SVN
SERVER_HOST=neptuno.uca.es

# URL to the prefix of the Nexus REST service API for
# resolving/downloading artifacts by GAV coordinates
NEXUS_ARTIFACT_PREFIX="https://${SERVER_HOST}/nexus/service/local/artifact/maven"

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

function download_from_nexus() {
    if [ "$#" != 1 ]; then
        error "Usage: download_from_nexus (REST API query string)"
        error "See Nexus documentation for details (http://j.mp/dCuZxL) for:"
        error "  - /artifact/maven/content"
        error "  - /artifact/maven/resolve"
        return 1
    fi
    QUERY="$1"
    status "Fetching Nexus artfact matching $QUERY..."
    push_d "$DL_CACHE"

    URL="$NEXUS_ARTIFACT_PREFIX/redirect?$QUERY"
    status "Visiting URL $URL..."

    # Download the artifact into the cache, using Nexus' redirects and
    # wget's timestamping to get the name of the file and avoid
    # downloading the same thing over and over. Notes:
    #
    #                 LC_ALL: we need C locale to parse wget's output reliably.
    # --no-check-certificate: TERENA SSL certificate is not trusted in openSUSE.
    #  --content-disposition: use the filename proposed by Nexus.
    #         --timestamping: do not download if the existing file is newer
    BASENAME=$(LC_ALL="C" \
        wget --no-check-certificate --content-disposition --timestamping \
        "$URL" 2>&1 \
        | tee | grep '^Location' | awk '{print $2}' | xargs basename)
    if test "$?" != 0; then
        error "Could not download artifact matching $QUERY"
        return 1
    fi

    DEST_PATH="$DL_CACHE/$BASENAME"
    status "Nexus artifact downloaded to $DEST_PATH"
    pop_d
    echo "$DEST_PATH"
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

### Main body

CATALINA_HOME=$HOME/bin/dynamo-tomcat

### Download and install Tomcat

TOMCAT_BIN_NEXUS="r=thirdparty&g=org.apache&a=tomcat&v=5.5.26&c=dist&p=zip"
TOMCAT_DIST=$(download_from_nexus "$TOMCAT_BIN_NEXUS")

confirm_rm "$CATALINA_HOME"
push_d "$CATALINA_HOME"
unzip "$TOMCAT_DIST"
mv */* .
pop_d

### Install Dynamo

DIST_P=org.activebpel.rt.dist
DEMO=dynamo-demo/tomcat
BPEL=PizzaDeliveryCompany
BPR="$BPEL.bpr"

mvn -am -pl "$DIST_P" install
tar -xzf "$DIST_P"/target/*-tomcat.tar.gz -C "$CATALINA_HOME"
chmod +x "$CATALINA_HOME"/bin/*.sh

# Replace "8xxx" by "7xxx" in server.xml, to change the port Tomcat listens to
sed -i -r -e 's/8([0-9]+)/7\1/g' "$CATALINA_HOME"/{conf/server.xml,bin/ActiveBPEL.sh}

# Zip the composition and install it

push_d "$DEMO/$BPEL"
zip -r "$BPR" bpel META-INF wsdl
cp -v "$BPR" "$CATALINA_HOME/bpr"
pop_d
