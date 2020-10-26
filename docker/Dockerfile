FROM rtdi/connectorrootapp

LABEL maintainer="info@rtdi.io"

ADD https://raw.githubusercontent.com/rtdi/HanaConnector/master/docker/global.properties conf/rtdiconfig/HanaConnector/
ADD https://github.com/rtdi/HanaConnector/releases/latest/download/hanaconnector.war ./webapps/

EXPOSE 8080
EXPOSE 8443


CMD ["appcontainer.sh"]
