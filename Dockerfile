FROM alpine:3.4
RUN apk update && apk add openjdk8-jre wget bash
RUN cd /usr/bin && wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
RUN [ "$(md5sum /usr/bin/lein)" = "b7a342b68140f5452486457a92a64df2  /usr/bin/lein" ]
RUN chmod 755 /usr/bin/lein
RUN adduser -S grimmly
RUN mkdir /grimmly
RUN chown -R grimmly /grimmly
USER grimmly
WORKDIR /grimmly
RUN lein
ADD src /grimmly/src
ADD project.clj /grimmly/project.clj
RUN lein uberjar
CMD java -Ddebug=true -Dip=0.0.0.0 -jar /grimmly/target/uberjar/grimmly-0.1.0-SNAPSHOT-standalone.jar
