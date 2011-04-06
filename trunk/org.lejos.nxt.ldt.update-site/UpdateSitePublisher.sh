#!/bin/bash
## Push a complete update site to a local Tomcat install so that you can use a
# URL of the following form within Eclipse:
# http://localhost:8080/update
# The directory that gets pushed to Tomcat's webapps directory is exactly what needs 
# to be published on the public web site as the "update site".

# Until we convert this into a grownup Ant script, please modify the
# following variables to match your environment. If you've set these
# in your .profile, you don't need to change it here.
[ -z "$ECLIPSE_HOME" ] && ECLIPSE_HOME=/Applications/helios
[ -z "$CATALINA_HOME" ] && CATALINA_HOME=/usr/share/tomcat6

# Assume that this script is being run from within the workspace
CURRENT_DIR=`dirname "$0"`
[ -z "$WORKSPACE" ] && WORKSPACE=`cd "$CURRENT_DIR/.." ; pwd`

PROJECT=${WORKSPACE}/org.lejos.nxt.ldt.update-site
WEBAPP=${CATALINA_HOME}/webapps/lejos/update

java \
	-jar ${ECLIPSE_HOME}/plugins/org.eclipse.equinox.launcher_*.jar \
	-application org.eclipse.equinox.p2.publisher.UpdateSitePublisher \
	-metadataRepository file:${WEBAPP} \
    -metadataRepositoryName "LeJOS Update Site" \
	-artifactRepository file:${WEBAPP} \
    -artifactRepositoryName "LeJOS Eclipse Plugin" \
	-source ${PROJECT} \
	-configs gtk.linux.x86 \
	-compress \
	-publishArtifacts
	
java \
    -jar ${ECLIPSE_HOME}/plugins/org.eclipse.equinox.launcher_*.jar \
    -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
    -metadataRepository file:${WEBAPP} \
    -categoryDefinition file:${PROJECT}/category.xml	

cp ${PROJECT}/site.xml ${WEBAPP}