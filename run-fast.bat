@ECHO off
set MAVEN_OPTS_BACKUP=%MAVEN_OPTS%
set MAVEN_OPTS=

del /F /Q work\plugins
call mvn -Dmaven.test.skip=true -DskipTests=true clean hpi:run <nul

set MAVEN_OPTS=%MAVEN_OPTS_BACKUP%
