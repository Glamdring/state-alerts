Deploying
======================

Make sure you have installed MySQL and Tomcat (at least 7). Nginx / apache with forwarding to Tomcat is also desirable.

Copy the war file from _webapp/target_ into the _webapps_ server folder as _ROOT.war_

In _$TOMCAT/conf/server.xml_ find the _Connector_ element and add _URIEncoding="utf-8"_ attribbute so it looks like

    <Connector port="..."
               protocol="HTTP/1.1"
               connectionTimeout="20000"
               URIEncoding="UTF-8"
               redirectPort="8443"/>

And also add the following at the bottom of the _Engine_ element:

    <Host name="yourdomain.com" appBase="webapps" unpackWARs="true" autoDeploy="true">
        <Alias>www.yourdomain.com</Alias>
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs/access"  prefix="welshare_access_" suffix=".log" pattern="%h %l %u %t &quot;%r&quot; %s %b, t=%D" resolveHosts="false"/>
        <Valve className="org.apache.catalina.valves.StuckThreadDetectionValve" threshold="60" />
    </Host>

Also change the defaultHost of your Engine:

    <Engine name="Catalina" defaultHost="yourdomain.com">
    

Create a config folder (e.g. /var/config) and copy there all files from _project-root/config_ (at least the following configuration files _statealert.properties_, _extractors.json_, _ehcache.xml_)

Add the following in catalina.sh

    export CATALINA_OPTS="-Dstatealerts.config.location=/var/config -Duser.timezone=UTC"
  