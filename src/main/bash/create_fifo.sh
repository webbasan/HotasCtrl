#!/usr/bin/env bash
#
# Create named pipe ("fifo") for use by HotasCtrl daemon. webbasan, 2017-10-28

FIFO_NAME=/tmp/hotas.fifo       # TODO: allow overwriting from environment

mkfifo $FIFO_NAME
