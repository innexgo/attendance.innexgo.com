#!/bin/bash

set -e

echo Starting the 8080 to 8443 node server
cd ./redirectSecureServer
node app.js &
cd ..

echo Please enter password for innexgo.com key below:
read -s PASSWORD

java -jar \
	-Dserver.port=8443 \
	-Dserver.ssl.enabled=true \
	-Dserver.ssl.key-store=/etc/letsencrypt/live/innexgo.com/keystore.p12 \
	-Dserver.ssl.key-store-password=$PASSWORD \
	-Dserver.ssl.keyStoreType=PKCS12 \
	-Dserver.ssl.keyAlias=tomcat \
	./build/libs/innexgo-0.0.1-SNAPSHOT.jar > ~/logs.txt &

# Set redirects
echo Setting port redirects 
sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 443 -j REDIRECT --to-port 8443
