FROM jboss/wildfly:10.1.0.Final

ENV DEPLOYMENT_DIR ${JBOSS_HOME}/standalone/deployments/

ADD target/partyservice.war ${DEPLOYMENT_DIR}