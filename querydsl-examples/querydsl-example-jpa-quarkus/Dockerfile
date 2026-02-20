FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 AS build
COPY --chown=quarkus:quarkus . /code/
USER quarkus
WORKDIR /code

RUN mkdir -p querydsl-libraries/querydsl-core/target/test-classes
RUN ./mvnw -ntp -B install -Pquickbuild -pl :querydsl-jpa -am -Dtoolchain.skip -Dgcf.skip -Dmaven.test.skip=true
RUN ./mvnw verify -Dnative -Pexamples -pl :querydsl-example-jpa-quarkus -Dtoolchain.skip -Dgcf.skip -Dlatest.java.version=21
