
repositories {
    mavenLocal()
    mavenCentral()
    maven( url ="https://jcenter.bintray.com")
}


plugins {
    `java-library`
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


dependencyManagement  {
    imports {
        mavenBom("io.micronaut:micronaut-bom:1.3.4")
    }
}

val EVOMASTER_VERSION = "1.2.2-SNAPSHOT"

dependencies{
    implementation("org.evomaster:evomaster-client-java-controller:$EVOMASTER_VERSION")
    implementation("org.testcontainers:testcontainers:1.15.2")
    implementation(project(":cs:graphql:patio-api"))

    implementation("io.micronaut.data:micronaut-data-tx:1.0.2")
}