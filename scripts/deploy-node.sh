:
#!/bin/bash

set -e

echo Starting the 8080 to 8443 node server
cd ./redirectSecureServer
node app.js &
cd ..
