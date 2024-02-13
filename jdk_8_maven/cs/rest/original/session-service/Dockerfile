#
# Copyright (c) 2019 The Hyve B.V.
# This code is licensed under the GNU Affero General Public License (AGPL),
# version 3, or (at your option) any later version.
#

FROM maven:3-eclipse-temurin-11 as build
COPY $PWD /session-service
WORKDIR /session-service
RUN mvn package -DskipTests -Dpackaging.type=jar

FROM eclipse-temurin:11
# copy over target/session_service-x.y.z.jar ignore *-model.jar, that jar is
# used by cbioportal/cbioportal to import the models
COPY --from=build /session-service/target/*[0-9].jar /app.war
CMD java ${JAVA_OPTS} -jar /app.war
