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
# NOTE: setting text requires quoting of spaces etc. -> pointer to URL encoding
curl http://localhost:$HTTP_PORT/line1/Elite:%20Dangerous
curl http://localhost:$HTTP_PORT/line2/%20%20%20%20Welcome
curl http://localhost:$HTTP_PORT/line3/%20Cmdr%20%20Webbasan

# TODO: accept more URL standard conforming syntax, allowing to set multiple items of the same subsystem:
#   /light?all=half   \\ /light?mfd=half&led=half
#   /led?all=amber    \\ /led?A=amber&B=amber&D=amber&E=amber&I=red&T1=red&T2=amber&T3=green&POV=green&FIRE=on&THROTTLE=on
#   /text?line1=...&line2=...&line3=...
#   /clock?mode=local_24h
