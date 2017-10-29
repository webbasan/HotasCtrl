#!/usr/bin/env bash
#
# Stop HotasCtrl daemon in background using named pipe ("fifo"). webbasan, 2017-10-29

FIFO_NAME=/tmp/hotas.fifo       # TODO: allow overwriting from environment

echo >$FIFO_NAME "quit"
