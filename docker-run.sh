#!/bin/bash

set -e

TAG=$(docker images --format "{{.Tag}}" "robot-advisor"|head -1)
docker run -it robot-advisor:$TAG



