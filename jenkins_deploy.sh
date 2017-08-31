#!/usr/bin/env bash
ssh ralf@appserver1 ' fuser -k 8080/tcp '
scp ./target/blockchain_server_* ralf@appserver1:~/deploy/
ssh ralf@appserver1 ' nohup java -Dserver.port=8080 -jar ./deploy/blockchain_server_*.jar  >blockchain.log &'