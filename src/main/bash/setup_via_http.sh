#!/usr/bin/env bash
#
# Setup HotasCtrl daemon via HTTP requests. webbasan, 2017-10-29

HTTP_PORT=8080                  # TODO: allow overwriting from environment

curl http://localhost:$HTTP_PORT/light/all/half
curl http://localhost:$HTTP_PORT/led/all/amber
curl http://localhost:$HTTP_PORT/led/I/red
curl http://localhost:$HTTP_PORT/led/POV/green
curl http://localhost:$HTTP_PORT/led/T1/red
curl http://localhost:$HTTP_PORT/led/T2/amber
curl http://localhost:$HTTP_PORT/led/T3/green
curl http://localhost:$HTTP_PORT/clock/local_24h
# TODO: setting text requires quoting of spaces etc.