About Dynamo
------------

This branch contains a repackaged version of the Dynamo-AOP version of
the ActiveBPEL WS-BPEL engine, updated to work with Java 6 and the
UCASE fork of ActiveBPEL 4.1. The original source code and binaries
are available from this website:

  http://plastic.isti.cnr.it/wiki/tools#dynamo-aop

This version is provided on an as-is basis and is strictly a research
prototype. Nevertheless, users wishing to try it on a recent
Debian-based GNU/Linux system can install it by following these steps:

- Create a local 'antonio' account on the computer, for the email
  notifications.

- Install the 'postfix' package, allowing local delivery of
  emails. This is also needed so the 'notify' recovery rules work as
  expected.

- Run the installation scripts for JBoss and ActiveBPEL:

    ./install-jboss.sh
    ./install-tomcat.sh

- To launch Dynamo, open two terminals. In the first terminal, run:

    cd ~/bin/jboss-4.2.3.GA && bin/run.sh

  In the second terminal, run:

    cd ~/bin/dynamo-tomcat && CATALINA_RUN=$(pwd) bin/catalina.sh run

- Wait 10-20 seconds for both servers to fully start up.

- Go to http://localhost:8080/dynamo and try out the Pizza Delivery demo.
