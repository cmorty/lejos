#!/bin/bash
## Generates all the files need for the update site and puts them into
# the dist directory. From here, all you need to do is copy them to 
# the live update site.

# Until we convert this into a grownup Ant script, please modify the
# following variables to match your environment. If you've set these
# in your .profile, you don't need to change it here.
[ -z "$ECLIPSE_HOME" ] && ECLIPSE_HOME=/Applications/helios

# Assume that this script is being run from within the workspace
CURRENT_DIR=`dirname "$0"`
[ -z "$WORKSPACE" ] && WORKSPACE=`cd "$CURRENT_DIR/.." ; pwd`

PROJECT=${WORKSPACE}/org.lejos.nxt.ldt.update-site
DIST=${PROJECT}/dist

java \
	-jar ${ECLIPSE_HOME}/plugins/org.eclipse.equinox.launcher_*.jar \
	-application org.eclipse.equinox.p2.publisher.UpdateSitePublisher \
	-metadataRepository file:${DIST} \
    -metadataRepositoryName "LeJOS Update Site" \
	-artifactRepository file:${DIST} \
    -artifactRepositoryName "LeJOS Eclipse Plugin" \
	-source ${PROJECT} \
	-configs gtk.linux.x86 \
	-compress \
	-publishArtifacts
	
java \
    -jar ${ECLIPSE_HOME}/plugins/org.eclipse.equinox.launcher_*.jar \
    -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
    -metadataRepository file:${DIST} \
    -categoryDefinition file:${PROJECT}/category.xml	

cp ${PROJECT}/site.xml ${DIST}