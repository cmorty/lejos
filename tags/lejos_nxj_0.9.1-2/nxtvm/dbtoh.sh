#!/bin/bash

to_upper()
{
	echo "$1" | tr '[:lower:]' '[:upper:]'
}

convert_class()
{
	local TMP="$(to_upper "_${1//./_}")"
	local LINE

	echo "/**"
	echo " * Machine-generated file. Do not modify."
	echo " */"
	echo "#ifndef $TMP"
	echo "#define $TMP"
	echo
	
	local i=0
	while read -r LINE; do
		if [[ ! "$LINE" =~ ^[[:space:]]*\# ]] && [[ "$LINE" =~ [^[:space:]] ]]; then
			LINE="$(to_upper "$LINE")"
			LINE="${LINE//;/}"
			LINE="${LINE//[/A}"
			LINE="${LINE//\//_}"
			echo "#define $LINE $i"
			let i++
		fi
	done
	echo "#define NUM_SPECIAL_CLASSES $i"
	
	echo
	echo "#endif" 
}

convert_signature()
{
	local TMP="$(to_upper "_${1//./_}")"
	local LINE

	echo "/**"
	echo " * Machine-generated file. Do not modify."
	echo " */"
	echo "#ifndef $TMP"
	echo "#define $TMP"
	echo
	
	local i=0
	while read -r LINE; do
		if [[ ! "$LINE" =~ ^[[:space:]]*# ]] && [[ "$LINE" =~ [^[:space:]] ]]; then
			LINE="${LINE//_/_0}"
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
OUT="$3"
OUTNAME="$(basename -- "$OUT")"


case "$1" in
	class)
		convert_class "$OUTNAME" <"$IN" >"$OUT"
		;;
	signature)
		convert_signature "$OUTNAME" <"$IN" >"$OUT"
		;;
	*)
		echo "unknown type"
		exit 1
esac
