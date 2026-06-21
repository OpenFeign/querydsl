# Optional pre-built Querydsl artifacts, injected by CI's "compile" job via
#   --build-context m2=<dir with io/github/openfeign/querydsl> --build-arg REUSE_M2=true
# so the image build does not have to recompile Querydsl from source. Empty by default,
# so a plain `docker build` still works standalone (it rebuilds Querydsl itself).
FROM scratch AS m2

FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-25 AS build
ARG REUSE_M2=false
COPY --chown=quarkus:quarkus . /code/
COPY --from=m2 --chown=quarkus:quarkus . /home/quarkus/.m2/repository/
USER quarkus
WORKDIR /code

# Build Querydsl from source only when prebuilt artifacts were not injected.
RUN if [ "$REUSE_M2" != "true" ]; then \
      ./mvnw -ntp -B install -Pquickbuild -pl :querydsl-jpa -am -Dtoolchain.skip -Dgcf.skip ; \
    fi
RUN ./mvnw -ntp -B verify -Dnative -Pexamples -pl :querydsl-example-jpa-quarkus -Dtoolchain.skip -Dgcf.skip
