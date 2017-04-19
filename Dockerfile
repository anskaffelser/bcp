FROM maven:3-jdk-8

ADD . $MAVEN_HOME/project

RUN cd $MAVEN_HOME/project \
 && mvn -B clean package -Pdist \
 && mv $MAVEN_HOME/project/target/virksert-server /virksert \
 && rm -r $MAVEN_HOME/project \
 && mkdir /virksert/conf /virksert/ext /virksert/cache

VOLUME /virksert/conf

EXPOSE 8080

WORKDIR /virksert

ENTRYPOINT ["sh", "/virksert/bin/run.sh"]
