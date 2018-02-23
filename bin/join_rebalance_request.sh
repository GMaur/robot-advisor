#!/bin/bash

set -e

portfolio=$(cat $1)
ideal=$(cat $2)

#jq '[.[] | {current: $portfolio, ideal: $ideal}]'
echo "{"
echo -n "\"current\":"
cat $1
echo ","
echo -n "\"ideal\":"
cat $2
echo "}"
