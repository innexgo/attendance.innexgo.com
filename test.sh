#!/bin/sh

set -e

CREATORID=1
EPOCHTIME=2147483647
PASSWORD=1234


curl "localhost:8080/apiKey/new/?creatorId=$CREATORID&expirationTime=$EPOCHTIME&password=$PASSWORD"



