Deploying on Openshift
======================

Register for openshift account and create a scaling tomcat 7 app with mysql support

    rhc app create -a statealerts -s jbossews-2.0 mysql-5.1

Delete the _src_ folder and _pom.xml_ file from the template

    cd statealerts
    git rm -r src/ pom.xml

Copy the prebuid war into _webapps_ folder as _ROOT.war_

    cp <path-to-source>/webapp/target/statealerts-0.0.1-SNAPSHOT.war webapps/ROOT.war

In _.openshift/action_hooks_ folder create an executable called _pre_start_jbossews_ with following contents

    export CATALINA_OPTS="-Dstatealerts.config.location=${OPENSHIFT_REPO_DIR}/config"
    
In repository root folder create folder called _config_ and copy there at least following configuration files _statealert.properties_, _extractors.json_, _ehcache.xml_.

Change the contents of _statealerts.properties_ to look like

    database.driverClassName=com.mysql.jdbc.Driver
    database.url=jdbc\:mysql\://${OPENSHIFT_MYSQL_DB_HOST}:${OPENSHIFT_MYSQL_DB_PORT}/${OPENSHIFT_APP_NAME}?characterEncoding=utf8
    database.username=${OPENSHIFT_MYSQL_DB_USERNAME}
    database.password=${OPENSHIFT_MYSQL_DB_PASSWORD}

    index.path=${OPENSHIFT_DATA_DIR}/index
    lucene.analyzer.class=org.apache.lucene.analysis.bg.BulgarianAnalyzer

    random.sleep.max.minutes=10

    ui.locale=bg

To deploy the application execute

    git push
