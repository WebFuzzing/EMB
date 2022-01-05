FROM huygensing/timbuctoo:buildbase-11

COPY ./ContractDiff ./ContractDiff
COPY ./HttpCommand ./HttpCommand
COPY ./security-client-agnostic ./security-client-agnostic
COPY ./timbuctoo-test-services ./timbuctoo-test-services
COPY ./timbuctoo-instancev4/src ./timbuctoo-instancev4/src
COPY ./timbuctoo-instancev4/pom.xml ./timbuctoo-instancev4/pom.xml
COPY ./pom.xml ./pom.xml

COPY ./timbuctoo-instancev4/example_config.yaml ./timbuctoo-instancev4/example_config.yaml
RUN mvn clean package

FROM openjdk:11-jre-slim

WORKDIR /app

RUN mkdir -p /root/data/dataSets && \
  mkdir -p /root/data/neo4j && \
  mkdir -p /root/data/auth/authorizations && \
  echo "[]" > /root/data/auth/logins.json && \
  echo "[]" > /root/data/auth/users.json

COPY --from=0 /build/timbuctoo/timbuctoo-instancev4/target/appassembler .
COPY --from=0 /build/timbuctoo/timbuctoo-instancev4/example_config.yaml .

CMD ["./bin/timbuctoo", "server", "./example_config.yaml"]

EXPOSE 80 81
ENV timbuctoo_port="80"
ENV timbuctoo_adminPort="81"
ENV base_uri=http://localhost:8080

ENV timbuctoo_elasticsearch_host=http://example.com/elasticsearchhost
ENV timbuctoo_elasticsearch_port=80
ENV timbuctoo_elasticsearch_user=user
ENV timbuctoo_elasticsearch_password=password

ENV timbuctoo_dataPath="/root/data"
ENV timbuctoo_authPath="/root/data/auth"

ENV timbuctoo_search_url=http://localhost:8082
ENV timbuctoo_indexer_url=http://indexer
