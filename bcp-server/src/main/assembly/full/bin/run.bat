@echo off

cd %0\..\..

java -classpath .;ext/*;lib/*;conf no.difi.bcp.server.Application %*