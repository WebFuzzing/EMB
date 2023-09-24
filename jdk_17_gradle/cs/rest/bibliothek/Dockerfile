ARG JAVA_VERSION=17
ARG JVM_FLAVOR=hotspot

FROM openjdk:${JAVA_VERSION}-jdk-slim AS builder
WORKDIR /build

COPY ./ ./
RUN ./gradlew clean buildForDocker --no-daemon


ARG JAVA_VERSION
ARG JVM_FLAVOR

FROM openjdk:${JAVA_VERSION}-slim
WORKDIR /app

RUN groupadd --system bibliothek \
    && useradd --system bibliothek --gid bibliothek \
    && chown -R bibliothek:bibliothek /app
USER bibliothek:bibliothek

VOLUME /data/storage
EXPOSE 8080

# We override default config location search path,
# so that a custom file with defaults can be used
# Normally would use environment variables,
# but they take precedence over config file
# https://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/html/boot-features-external-config.html
ENV SPRING_CONFIG_LOCATION="optional:classpath:/,optional:classpath:/config/,file:./default.application.yaml,optional:file:./,optional:file:./config/"
COPY ./docker/default.application.yaml ./default.application.yaml

COPY --from=builder /build/build/libs/docker/bibliothek.jar ./
CMD ["java", "-jar", "/app/bibliothek.jar"]
