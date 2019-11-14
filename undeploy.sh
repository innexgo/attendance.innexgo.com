#!/bin/bash

if [ $EUID != 0 ]; then
	echo "please run as root"
	exit 1
fi

killatport() {
	PID=$(lsof -t -i:$1)
	# If it completed successfully we kill it
	if [[  $? == 0 ]]
	then
		echo port $1 in use, killing $PID
		kill $PID
	else
		echo port $1 not in use
	fi
}

# Kill all common ones
killatport 80
killatport 443
killatport 8080
killatport 8443
