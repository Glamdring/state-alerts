Deploying on Openshift
======================

Register for openshift account and create a tomcat 7 app with mysql support. Execute the following command in a newly-created directory (e.g. statealerts-deployment)

    rhc app create -a statealerts jbossews-2.0 mysql-5.1

Clone the openshift git repo:

    git clone <your repo>
    
Delete the _src_ folder and _pom.xml_ file from the template

    cd statealerts
    git rm -r src/ pom.xml

Copy the prebuid war into _webapps_ folder as _ROOT.war_

    cp <path-to-source>/webapp/target/statealerts-0.0.1-SNAPSHOT.war webapps/ROOT.war

In _.openshift/config/server.xml_ find _Connector_ element and add _URIEncoding="utf-8"_ attribbute so it looks like

    <Connector address="${OPENSHIFT_JBOSSEWS_IP}"
               port="${OPENSHIFT_JBOSSEWS_HTTP_PORT}"
               protocol="HTTP/1.1"
               connectionTimeout="20000"
               URIEncoding="utf-8"
               redirectPort="8443"/>

In _.openshift/action_hooks_ folder create a file called _pre_start_jbossews_ with following contents

    export CATALINA_OPTS="-Dstatealerts.config.location=${OPENSHIFT_REPO_DIR}/config -Duser.timezone=UTC"
    
In repository root folder create folder called _config_ and copy there all files from _project-root/config_ (at least the following configuration files _statealert.properties_, _extractors.json_, _ehcache.xml_)

Commit your changes

    git add webapps config .openshift
    git commit  -m "Inital commit for state alerts app."

To deploy the application execute

    git push
