plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.gitlab.arturbosch.detekt") version("1.23.8")
}

group = "jp.unaguna"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-context:5.3.39")
    implementation("org.jcommander:jcommander:1.85")
    testImplementation(kotlin("test"))
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
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

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
}
