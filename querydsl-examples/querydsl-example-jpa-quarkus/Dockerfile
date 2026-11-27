# Optional local Maven repository (with the pre-built Querydsl artifacts) and wrapper, injected by CI via
#   --build-context m2=<repository> --build-context mvnw=<wrapper> --build-arg REUSE_M2=true
# so the image build does not have to recompile Querydsl from source. Empty by default,
# so a plain `docker build` still works standalone (it rebuilds Querydsl itself).
FROM scratch AS m2
FROM scratch AS mvnw

FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-25 AS build
ARG REUSE_M2=false
ARG MAVEN_ARGS
ARG MVNW_VERBOSE
COPY --chown=quarkus:quarkus . /code/
COPY --from=m2 --chown=quarkus:quarkus . /home/quarkus/.m2/repository/
COPY --from=mvnw --chown=quarkus:quarkus . /home/quarkus/.m2/wrapper/
USER quarkus
WORKDIR /code

# Build Querydsl from source only when prebuilt artifacts were not injected.
RUN if [ "$REUSE_M2" != "true" ]; then \
      ./mvnw -ntp -B install -Pquickbuild -pl :querydsl-jpa -am -Dtoolchain.skip -Dgcf.skip ; \
    fi
RUN ./mvnw -ntp -B verify -Dnative -Pexamples -pl :querydsl-example-jpa-quarkus -Dtoolchain.skip -Dgcf.skip
