#!/usr/bin/env bash
#
# Remove named pipe ("fifo") used by HotasCtrl daemon. webbasan, 2017-10-28

FIFO_NAME=/tmp/hotas.fifo       # TODO: allow overwriting from environment

rm $FIFO_NAME
