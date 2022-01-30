#!/bin/bash

java -jar \
	-Dserver.port=7080 \
	./build/libs/innexgo-0.0.1-SNAPSHOT.jar > ~/logs.txt &

