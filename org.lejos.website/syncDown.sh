#!/bin/bash

rsync -aP --delete \
	--exclude=**/CVS \
	--exclude=**/Thumbs.db \
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
	skoehler,lejos@web.sourceforge.net:htdocs/ htdocs/

