#!/bin/bash

# for OSX
function my_resolve() {
	if [ "${2:0:1}" == "/" ]; then
		echo "$2"
	elif [ "$2" == "." ]; then
		echo "$1"
	elif [ "$1" == "." ]; then
		echo "$2"
	else
		echo "$1/$2"
	fi
}
function my_readlink() {
	local TMP1="$(dirname -- "$1")"
	local TMP2="$(readlink -- "$1")"
	my_resolve "$TMP1" "$TMP2"
}
function my_build_cp() {
	local TMP_CP="$(find "$1" -name "*.jar" -print0 | tr "\0" "$SEP")"
	# remove last $SEP 
	echo ${TMP_CP%?}
}

NXJ_COMMAND="$(basename -- "$0")"
if [ -n "$NXJ_HOME" ]; then
	NXJ_BIN="$NXJ_HOME/bin"
else
	NXJ_BIN="$0"
	while [ -L "$NXJ_BIN" ]; do
		NXJ_BIN="$(my_readlink "$NXJ_BIN")"
	done
	NXJ_BIN="$(dirname -- "$NXJ_BIN")"
	NXJ_BIN="$(my_resolve "$(pwd)" "$NXJ_BIN")"
	NXJ_HOME="$NXJ_BIN/.."
fi

if [ -n "$LEJOS_NXT_JAVA_HOME" ]; then
	JAVA="$LEJOS_NXT_JAVA_HOME/bin/java"
	JAVAC="$LEJOS_NXT_JAVA_HOME/bin/javac"
elif [ -n "$JAVA_HOME" ]; then
	JAVA="$JAVA_HOME/bin/java"
	JAVAC="$JAVA_HOME/bin/javac"
else
	JAVA="java"
	JAVAC="javac"
fi

SEP=":"
NXJ_FORCE32=""
OS_KERNEL="$(uname -s)"

if [ "${OS_KERNEL:0:6}" == "CYGWIN" ]; then
	SEP=";"
elif [ "${OS_KERNEL}" == "Darwin" ]; then
    NXJ_FORCE32="-d32"
fi

NXJ_CP_PC="$(my_build_cp "$NXJ_HOME/lib/pc")"
NXJ_CP_NXT="$(my_build_cp "$NXJ_HOME/lib/nxt")"
