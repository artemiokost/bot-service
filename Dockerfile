FROM openjdk:8-jre-alpine

ENV VERTICLE_FILE build/libs/bot-service.jar

# Set the location of the verticles
ENV VERTICLE_HOME /opt/verticles

EXPOSE 8004

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY src/config/docker.json $VERTICLE_HOME/


WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar bot-service.jar -conf docker.json"]
