#!/bin/bash

# for OSX
function my_resolve() {
	if [[ "$2" =~ ^/ ]]; then
		echo "$2"
	elif [[ "$2" == "." ]]; then
		echo "$1"
	elif [[ "$1" == "." ]]; then
		echo "$2"
	else
		echo "$1/$2"
	fi
}
function my_readlink() {
	if [ -L "$1" ]; then
		local TMP1="$(dirname -- "$1")"
		local TMP2="$(readlink -- "$1")"
		my_resolve "$TMP1" "$TMP2"
	else
		echo "$1"
	fi
}

NXJ_COMMAND="$(basename -- "$0")"
if [ -n "$NXJ_HOME" ]; then
	NXJ_BIN="$NXJ_HOME/bin"
else
	NXJ_BIN="$(my_readlink "$0")"
	NXJ_BIN="$(my_resolve "$(pwd)" "$NXJ_BIN")"
	NXJ_BIN="$(dirname -- "$NXJ_BIN")"
	NXJ_HOME="$NXJ_BIN/.."
fi

NXJ_LIBS="$NXJ_HOME/lib"
NXJ_LIBS_3rd="$NXJ_HOME/3rdparty/lib"

NXJ_JAR_BCEL="$NXJ_LIBS_3rd/bcel.jar"
NXJ_JAR_BLUECOVE="$NXJ_LIBS_3rd/bluecove.jar"
NXJ_JAR_BLUECOVE_GPL="$NXJ_LIBS_3rd/bluecove-gpl.jar"
NXJ_JAR_COMMONS_CLI="$NXJ_LIBS_3rd/commons-cli.jar"

NXJ_JAR_CLASSES="$NXJ_LIBS/classes.jar"
NXJ_JAR_JTOOLS="$NXJ_LIBS/jtools.jar"
NXJ_JAR_PCCOMM="$NXJ_LIBS/pccomm.jar"
NXJ_JAR_PCTOOLS="$NXJ_LIBS/pctools.jar"

SEP=":"
NXJ_FORCE32=""
NXJ_CP_BLUECOVE="$NXJ_JAR_BLUECOVE"
OS_KERNEL="$(uname -s)"

if [ "${OS_KERNEL:0:6}" == "CYGWIN" ]; then
	SEP=";"
fi
if [ "${OS_KERNEL}" == "Linux" ]; then
	NXJ_CP_BLUECOVE="$NXJ_JAR_BLUECOVE$SEP$NXJ_JAR_BLUECOVE_GPL"
fi
if [ "${OS_KERNEL}" == "Darwin" ]; then
    NXJ_FORCE32="-d32"
fi

NXJ_CP_BOOT="$NXJ_JAR_CLASSES"
NXJ_CP_LINK="$NXJ_JAR_BCEL$SEP$NXJ_JAR_COMMONS_CLI$SEP$NXJ_JAR_JTOOLS"
NXJ_CP_TOOL="$NXJ_CP_BLUECOVE$SEP$NXJ_CP_LINK$SEP$NXJ_JAR_PCCOMM$SEP$NXJ_JAR_PCTOOLS"

export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$NXJ_BIN"
export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH:$NXJ_BIN"
