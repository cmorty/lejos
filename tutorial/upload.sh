#!/bin/bash

rsync -aP --no-owner --no-group --no-perms release/ \
	$USER,lejos@web.sourceforge.net:/home/project-web/lejos/htdocs/nxt/nxj/tutorial/
