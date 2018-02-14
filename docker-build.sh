#!/bin/bash

set -e

VERSION=$(git log -n1 --format="%h")
docker build -t robot-advisor:$VERSION .



