FROM openjdk:8-jre-alpine

ENV VERTICLE_HOME /usr/verticles

COPY /verticles $VERTICLE_HOME

# Launch the verticle
WORKDIR $VERTICLE_HOME

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar vertx-pipeline-core-fat.jar -cp ."]
