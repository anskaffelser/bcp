FROM maven:3-jdk-8

ADD . $MAVEN_HOME

RUN cd $MAVEN_HOME \
 && mvn -B clean package -Pdist \
 && mv $MAVEN_HOME/target/virksert-server /virksert \
 && rm -r $MAVEN_HOME \
 && mkdir /virksert/conf /virksert/ext /virksert/cache

VOLUME /virksert/conf

EXPOSE 8080

WORKDIR /virksert

CMD ["sh", "/virksert/bin/run.sh"]