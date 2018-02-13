#!/bin/bash

set -e

TAG=$(docker images --format "{{.Tag}}" "robot-advisor")
docker run -it robot-advisor:$TAG



