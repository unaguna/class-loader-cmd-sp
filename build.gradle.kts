plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.gitlab.arturbosch.detekt") version("1.23.8")
}

group = "jp.unaguna"
version = "0.2.0-SNAPSHOT"

sourceSets {
    main {
        output.dir(
            mapOf("builtBy" to "generateVersionProperties"),
            layout.buildDirectory.dir("generated/resources"),
        )
    }
}


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
            archiveBaseName = archiveBaseName.get().removeSuffix("-sp")
            archiveClassifier = ""
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
}

tasks.register("generateVersionProperties") {
    val outputDir = file(layout.buildDirectory.dir("generated/resources"))
    inputs.property("version", version)
    outputs.dir(outputDir)

    doLast {
        val versionProperties = file("$outputDir/class-loader-cmd/version.properties")
        versionProperties.parentFile.mkdirs()
        versionProperties.writeText("version=$version")
    }
}.also {
    tasks.processResources { dependsOn(it) }
}
