#!/bin/sh

#if [ $AXIS_HOME =  ]:then
#   echo $AXIS_HOME not set
#   $AXIS_HOME=`dirname ..`
#   echo $AXIS_HOME
#fi

cd ..
AXIS2_CLASSPATH=./lib/axis2-M2.jar
AXIS2_CLASSPATH=$AXIS2_CLASSPATH:./lib/axis-wsdl4j-1.2.jar
AXIS2_CLASSPATH=$AXIS2_CLASSPATH:./lib/commons-logging-1.0.3.jar
AXIS2_CLASSPATH=$AXIS2_CLASSPATH:./lib/log4j-1.2.8.jar
AXIS2_CLASSPATH=$AXIS2_CLASSPATH:./lib/stax-1.1.1-dev.jar
AXIS2_CLASSPATH=$AXIS2_CLASSPATH:./lib/stax-api-1.0.jar
export AXIS2_CLASSPATH
echo the classpath $AXIS2_CLASSPATH
java -classpath $AXIS2_CLASSPATH org.apache.axis2.transport.http.SimpleHTTPServer $1 $2