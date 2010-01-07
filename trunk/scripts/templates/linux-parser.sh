#!/bin/bash

NXJ_CP_CMDLINE="$NXJ_CP_TOOL"
for (( i=1; i<=$#; i++ )); do
	case "${!i}" in
		#handle classpath parameters
		-cp|-classpath)
			(( i++ ))
			NXJ_CP_CMDLINE="$NXJ_CP_TOOL:${!i}"
			;;
		#handle other parameters that accept arguments
		-sourcepath|-bootclasspath|-extdirs|-endorseddirs|-processor|-processorpath|-d|-s|-encoding|-source|-target|-Xmaxerrs|-Xmaxwarns|-Xstdout)
			NXJ_CMDLINE[$i]="${!i}"
			(( i++ ))
			NXJ_CMDLINE[$i]="${!i}"
			;;
		#handle parameters without arguments
		-*)
			NXJ_CMDLINE[$i]="${!i}"
			;;
		#abort parsing at first non-parameter argument
		*)
			for (( ; i<=$#; i++ )); do
				NXJ_CMDLINE[$i]="${!i}"
			done
	esac
done

for i in -classpath "$NXJ_CP_CMDLINE" "${NXJ_CMDLINE[@]}"; do
	echo "out: $i"
done
