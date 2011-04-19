NOTE: This README.txt file only applies to the pom.xml file here in the top project, and in sub projects containing a pom.xml file.

*** THESE pom.xml FILES ARE NOT THE PRIMARY BUILD SYSTEM. The primary build is done via 'ant'.

These pom.xml files can be used to create a number of static code analysis reports (see: [mvn site] command below).
When viewing maven generated reports, be sure to directly open the individual site docs for each sub-project (<sub-project>/target/site/index.html).
Although the parent project docs show links to sub-projects, they are currently incomplete, and do not show most of the reports for the sub-projects.


To get started using maven (eg: to download and install maven) read this:
      http://maven.apache.org/download.html
      Installation instructions are lower down on the same page.

      Other good places to start reading:

        Maven in 5 Minutes: http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

        Maven Getting Started Guide: http://maven.apache.org/guides/getting-started/index.html


Command Line usage
-------------------

(Optional) Add maven (v2.2.x or newer, v3.0.3+ recommended) to your path (or launch via $MAVEN_HOME/bin/mvn.sh or .bat).

Move to the directory containing this README.txt file, and run:

mvn clean package

Maven creates all output in the 'target' directory. Sub-projects produce their output in their own 'target' directory.
To execute maven commands on only individual sub-projects, execute the maven command in the directory containing the sub-project pom.xml file.

Some other useful maven commands:
mvn compile                 - compile production classes
mvn compiler:testCompile    - compile unit test classes
mvn test                    - compile all classes, run unit tests
mvn package                 - compile all classes, run unit tests, build jar, and "patch" jar (that can be extacted into the CIS base install dir)
mvn install                 - install this projects jar into your local maven repository (${user.dir}/.m2/repository/...).
mvn scm:update              - fetch the latest source from the bank
mvn site                    - site docs will be created here: ./target/site/index.html

Add -X to any mvn command line to show more debug output.


IDE setup for Maven
-------------------
Eclipse:
There are several ways of making Maven work in Eclipse, including using m2eclipse (http://m2eclipse.codehaus.org/),
or running "mvn eclipse:eclipse" which generates a fresh .classpath file. (Be sure M2_REPO is defined in eclipse).
For details, see:
http://maven.apache.org/guides/mini/guide-ide-eclipse.html

IDEA:
IntelliJ IDEA (http://www.jetbrains.com/) can use the pom.xml as a project definition, via File -> Open Project and
opening the pom.xml file.

Netbeans:
http://maven.apache.org/guides/mini/guide-ide-netbeans/guide-ide-netbeans.html
