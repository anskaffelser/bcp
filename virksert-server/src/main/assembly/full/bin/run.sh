#!/bin/sh

cd $(dirname $(readlink -f $0))/..

java -classpath .:lib/*:conf no.difi.virksert.server.Application $@