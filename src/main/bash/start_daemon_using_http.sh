#!/usr/bin/env bash
#
# Run HotasCtrl daemon in background using HTTP server. webbasan, 2017-10-29

HTTP_PORT=8080                  # TODO: allow overwriting from environment
LOGFILE=hotasDaemon.log         # OS X/macOS: proper place would be something like ~/Library/Logs/HotasCtrl/hotasDaemon.log
HOTASCTRL=~/bin/HotasCtrl.jar   # TODO: allow overwriting from environment; may be set by "installer"

java -jar $HOTASCTRL >$LOGFILE http_port $HTTP_PORT &    # TODO: check for proper Java environment...
