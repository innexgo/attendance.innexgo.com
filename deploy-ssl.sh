#!/bin/bash

set -e

echo Please enter password for innexgo.com key below:
read -s PASSWORD

sudo java -jar \
	-Dserver.port=443 \
	-Dserver.ssl.enabled=true \
	-Dserver.ssl.key-store=/etc/letsencrypt/live/innexgo.com/keystore.p12 \
	-Dserver.ssl.key-store-password=$PASSWORD \
	-Dserver.ssl.keyStoreType=PKCS12 \
	-Dserver.ssl.keyAlias=tomcat \
	./build/libs/innexgo-0.0.1-SNAPSHOT.jar > ~/logs.txt &
