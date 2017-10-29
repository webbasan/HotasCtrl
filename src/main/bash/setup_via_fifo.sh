#!/usr/bin/env bash
#
# Setup HotasCtrl daemon via named pipe. webbasan, 2017-10-29

FIFO_NAME=/tmp/hotas.fifo       # TODO: allow overwriting from environment

echo >$FIFO_NAME "light all half"
echo >$FIFO_NAME "led all amber"
echo >$FIFO_NAME "led I red"
echo >$FIFO_NAME "led POV green"
echo >$FIFO_NAME "led T1 red"
echo >$FIFO_NAME "led T2 amber"
echo >$FIFO_NAME "led T3 green"
echo >$FIFO_NAME "clock local_24h"
echo >$FIFO_NAME "text \"Elite: Dangerous\n    Welcome\n Cmdr  Webbasan\""
