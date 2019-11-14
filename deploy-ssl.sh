#!/bin/bash

set -e

if [ $EUID != 0 ]; then
	echo "Please run script as root"
    	exit 1
fi 

echo Please enter password for innexgo.com key below:
read -s PASSWORD

java -jar \
	-Dserver.port=443 \
	-Dserver.port.http=80 \
	-Dserver.ssl.enabled=true \
	-Dserver.ssl.key-store=/etc/letsencrypt/live/innexgo.com/keystore.p12 \
	-Dserver.ssl.key-store-password=$PASSWORD \
	-Dserver.ssl.keyStoreType=PKCS12 \
	-Dserver.ssl.keyAlias=tomcat \
	./build/libs/innexgo-0.0.1-SNAPSHOT.jar > ~/logs.txt &
