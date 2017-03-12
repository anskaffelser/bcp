@echo off

cd %0\..\..

java -classpath .;lib/*;conf no.difi.virksert.server.Application %*