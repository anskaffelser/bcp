FROM maven:3.5.0-jdk-8 AS mvn

ADD . $MAVEN_HOME/project

RUN cd $MAVEN_HOME/project \
 && rm bcp-server/src/main/resources/application-dev.properties \
 && mvn -B clean package -Pdist \
 && mv $MAVEN_HOME/project/target/bcp-server /bcp \
 && find /bcp -name .gitkeep -exec rm -rf '{}' \;

WORKDIR /bcp

ENTRYPOINT ["sh", "/bcp/bin/run.sh"]



FROM java:8-jre-alpine

COPY --from=mvn /bcp /bcp

VOLUME /bcp/cache /bcp/conf /bcp/ext

EXPOSE 8080

WORKDIR /bcp

ENTRYPOINT ["sh", "/bcp/bin/run.sh"]
