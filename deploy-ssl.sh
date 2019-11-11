#!/bin/
java -jar \
	-Dserver.port=443 \
	-Dserver.ssl.enabled=true \
	-Dserver.ssl.key=/etc/letsencrypt/q \
	-Dserver.ssl.enabled=true \
	./build/libs/innexgo-0.0.1-SNAPSHOT.jar > ~/logs.txt &
