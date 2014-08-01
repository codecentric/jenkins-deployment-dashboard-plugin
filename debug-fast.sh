#! /bin/sh

rm -rf work/plugins
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n"
mvn clean hpi:run
