#!/usr/bin/env bash
#
# Stop HotasCtrl daemon in background using HTTP server. webbasan, 2017-10-29

HTTP_PORT=8080                  # TODO: allow overwriting from environment

curl http://localhost:$HTTP_PORT/quit
