#!/bin/bash

# set classpath to . by default (matches behaviour of java and javac)
NXJ_CMDLINE_CP="$NXJ_CP_PC$SEP."
for (( i=1; i<=$#; i++ )); do
	case "${!i}" in
		#handle classpath parameters
		-cp|-classpath)
			(( i++ ))
			NXJ_CMDLINE_CP="$NXJ_CP_PC$SEP${!i}"
			;;
		#handle other parameters that accept arguments
		-sourcepath|-bootclasspath|-extdirs|-endorseddirs|-processor|-processorpath|-d|-s|-encoding|-source|-target|-Xmaxerrs|-Xmaxwarns|-Xstdout)
			NXJ_CMDLINE[$i]="${!i}"
			(( i++ ))
			NXJ_CMDLINE[$i]="${!i}"
			;;
		#abort parsing at -jar or classname
		-jar|[!-]*)
			for (( ; i<=$#; i++ )); do
				NXJ_CMDLINE[$i]="${!i}"
			done
			;;
		#handle parameters without arguments
		*)
			NXJ_CMDLINE[$i]="${!i}"
	esac
done
