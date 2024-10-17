
repositories {
    mavenLocal()
    mavenCentral()
    maven( url ="https://jcenter.bintray.com")
}


plugins {
    `java-library`
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val EVOMASTER_VERSION = project.ext.get("EVOMASTER_VERSION")

/*dependencies{
    implementation("org.evomaster:evomaster-client-java-controller:$EVOMASTER_VERSION")
    implementation("org.evomaster:evomaster-client-java-dependencies:$EVOMASTER_VERSION"){
        exclude("com.github.tomakehurst")
    }

    api(project(":cs:rest:bibliothek"))

    //Gradle api() is not importing transitive dependencies???
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.1")
    implementation("org.mongodb:mongodb-driver-sync:4.9.1")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.1")
}

 */
dependencies {

    implementation("org.evomaster:evomaster-client-java-controller:$EVOMASTER_VERSION")
    implementation("org.evomaster:evomaster-client-java-dependencies:$EVOMASTER_VERSION"){
        exclude("com.github.tomakehurst")
    }

    api(project(":cs:rest:bibliothek"))

    //Gradle api() is not importing transitive dependencies???
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.1")
    implementation("org.mongodb:mongodb-driver-sync:4.9.1")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.1")
    testImplementation("org.springframework.security:spring-security-test")

    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor", "3.1.1")
    //checkstyle(libs.stylecheck)
    implementation(libs.jetbrains.annotations)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation("org.springframework.boot", "spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot", "spring-boot-starter-validation")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt:0.12.6")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")

    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security", "spring-security-test")
}