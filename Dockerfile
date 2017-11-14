FROM airhacks/glassfish:v5

ADD target/party-service.war ${DEPLOYMENT_DIR}