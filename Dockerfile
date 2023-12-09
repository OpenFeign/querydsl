FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
COPY --chown=quarkus:quarkus . /code/
USER quarkus
WORKDIR /code

RUN ./mvnw -ntp -B install -Pquickbuild -pl :querydsl-jpa -am -T2 -Dtoolchain.skip -Dgcf.skip
RUN ./mvnw verify -Dnative -Pexamples -pl :querydsl-example-jpa-quarkus -Dtoolchain.skip -Dgcf.skip
