#!/usr/bin/env bash
#
# Run HotasCtrl daemon in background using named pipe ("fifo"). webbasan, 2017-10-29

FIFO_NAME=/tmp/hotas.fifo       # TODO: allow overwriting from environment
LOGFILE=hotasDaemon.log         # OS X/macOS: proper place would be something like ~/Library/Logs/HotasCtrl/hotasDaemon.log
HOTASCTRL=~/bin/HotasCtrl.jar   # TODO: allow overwriting from environment; may be set by "installer"

java -jar $HOTASCTRL <$FIFO_NAME >$LOGFILE console &    # TODO: check for proper Java environment...
