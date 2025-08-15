plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "jp.unaguna"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-context:5.3.39")
    implementation("org.jcommander:jcommander:1.85")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = "jp.unaguna.classloader.sp.cmd.Main"
        }
    }
}
