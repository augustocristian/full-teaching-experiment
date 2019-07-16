#!/bin/sh

docker run -ti \
  --rm \
  -v ~/.elastest:/data \
  -v /var/run/docker.sock:/var/run/docker.sock \
  elastest/platform start \
  --server-address=156.35.119.57 \
  --testlink \
  --jenkins \
  --user=augusto \
  --password=elastest