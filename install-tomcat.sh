#!/bin/bash

set -e

if [ -z "$CATALINA_HOME" ]; then
    echo "Please set CATALINA_HOME to the Tomcat 5 installation directory." >&2
    exit 1
fi

DIST_P=org.activebpel.rt.dist
DEMO=dynamo-demo/tomcat
BPEL=PizzaDeliveryCompany
BPR="$BPEL.bpr"

mvn -am -pl "$DIST_P" clean install
tar -xzf "$DIST_P"/target/*-tomcat.tar.gz -C "$CATALINA_HOME"

# Replace "8xxx" by "7xxx" in server.xml, to change the port Tomcat listens to
sed -i -r -e 's/8([0-9]+)/7\1/g' "$CATALINA_HOME"/{conf/server.xml,bin/ActiveBPEL.sh}

# Copy over the Tomcat .war files
cp -v "$DEMO"/*.war "$CATALINA_HOME/webapps"

# Zip the composition and install it

pushd "$DEMO/$BPEL"
zip -r "$BPR" bpel META-INF wsdl
cp -v "$BPR" "$CATALINA_HOME/bpr"
popd
