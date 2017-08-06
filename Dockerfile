FROM maven:3-jdk-8

ADD . $MAVEN_HOME/project

RUN cd $MAVEN_HOME/project \
 && rm bcp-server/src/main/resources/application-dev.properties \
 && mvn -B clean package -Pdist \
 && mv $MAVEN_HOME/project/target/bcp-server /bcp \
 && rm -r $MAVEN_HOME/project \
 && mkdir /bcp/conf /bcp/ext /bcp/cache

VOLUME /bcp/cache /bcp/conf /bcp/ext

EXPOSE 8080

WORKDIR /bcp

ENTRYPOINT ["sh", "/bcp/bin/run.sh"]
