#!/bin/bash

convert_class()
{
	local TMP="$1"
	TMP=_"${1//./_}"
	TMP="${TMP^^}"

	echo "/**"
	echo " * Machine-generated file. Do not modify."
	echo " */"
	echo "#ifndef $TMP"
	echo "#define $TMP"
	echo
	
	local i=0
	while read -r LINE; do
		if [[ ! "$LINE" =~ ^[[:space:]]*# ]] && [[ "$LINE" =~ [^[:space:]] ]]; then
			LINE="${LINE^^}"
			LINE="${LINE//;/}"
			LINE="${LINE//[/A}"
			LINE="${LINE//\//_}"
			echo "#define $LINE $i"
			let i++
		fi
	done
	
	echo
	echo "#endif" 
}

convert_signature()
{
	local TMP="$1"
	TMP=_"${1//./_}"
	TMP="${TMP^^}"

	echo "/**"
	echo " * Machine-generated file. Do not modify."
	echo " */"
	echo "#ifndef $TMP"
	echo "#define $TMP"
	echo
	
	local i=0
	while read -r LINE; do
		if [[ ! "$LINE" =~ ^[[:space:]]*# ]] && [[ "$LINE" =~ [^[:space:]] ]]; then
			LINE="${LINE//[/_1}"
			LINE="${LINE//;/_2}"
			LINE="${LINE//\//_3}"
			LINE="${LINE//(/_4}"
			LINE="${LINE//)/_5}"
			LINE="${LINE//</_6}"
			LINE="${LINE//>/_7}"
			echo "#define $LINE $i"
			let i++
		fi
	done
	
	echo
	echo "#endif" 
}

IN="$2"
OUT="$IN.h"

case "$1" in
	class)
		convert_class "$OUT" <"$IN" >"$OUT"
		;;
	signature)
		convert_signature "$OUT" <"$IN" >"$OUT"
		;;
	*)
		echo "unknown type"
		exit 1
esac
