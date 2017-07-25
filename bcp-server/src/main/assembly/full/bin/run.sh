#!/bin/sh

cd $(dirname $(readlink -f $0))/..

java -classpath .:ext/*:lib/*:conf no.difi.bcp.server.Application $@