#!/bin/bash

# set classpath to . by default (matches behaviour of java and javac)
NXJ_CMDLINE_CP="$NXJ_CP_TOOL$SEP."
NXJ_CMDLINE_LP="$NXJ_BIN"
for (( i=1; i<=$#; i++ )); do
	case "${!i}" in
		#handle classpath parameters
		-cp|-classpath)
			(( i++ ))
			NXJ_CMDLINE_CP="$NXJ_CP_TOOL$SEP${!i}"
			;;
		#handle -Djava.library.path=
		-Djava.library.path=*)
			NXJ_CMDLINE_LP="$NXJ_BIN$SEP${!i:20}"
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
