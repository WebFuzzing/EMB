import org.gradle.jvm.tasks.Jar

//application {
//    mainClass.set("em.external.patio.ExternalEvoMasterController")
//}


repositories {
    mavenLocal()
    mavenCentral()
    maven( url ="https://jcenter.bintray.com")
}


plugins {
    `java-library`
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    application
   // id("com.github.johnrengelman.shadow") version "4.0.2"
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

//val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
//shadowJar.apply {
//    manifest.attributes.apply {
//        //put("Implementation-Title", "")
//        //put("Implementation-Version", "")
//        put("Main-Class", "em.external.patio.ExternalEvoMasterController")
//    }
//
//    baseName = project.name + "-all"
//}



val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-evomaster-runner"
    isZip64 = true
    manifest {
        attributes["Implementation-Title"] = "EM"
        attributes["Implementation-Version"] = "1.0"
        attributes["Main-Class"] = "em.external.patio.ExternalEvoMasterController"
        attributes["Premain-Class"] = "org.evomaster.client.java.instrumentation.InstrumentingAgent"
        attributes["Agent-Class"] = "org.evomaster.client.java.instrumentation.InstrumentingAgent"
        attributes["Can-Redefine-Classes"] = "true"
        attributes["Can-Retransform-Classes"] = "true"
    }
    from(configurations.runtimeClasspath.get().map{ if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
