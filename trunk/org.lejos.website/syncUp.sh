#!/bin/bash

USER="$1"
shift 1

rsync -aP "$@" \
	--exclude=**/.svn \
	--exclude=Assets_old \
	--exclude=apidocs \
	--exclude=eclipse \
	--exclude=forum \
	--exclude=forum3test \
	--exclude=icommand* \
	--exclude=nxt/nxj/tutorial \
	--exclude=nxt/nxj/api \
	--exclude=nxt/pc/api \
	--exclude=p_technologies \
	--exclude=rcx/api \
	--exclude=tools \
	--exclude=tutorial \
	htdocs/ "$USER",lejos@web.sourceforge.net:htdocs/

