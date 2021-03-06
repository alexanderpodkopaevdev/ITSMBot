plugins {
    application
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.podkopaev.alexander.itsm"
version = "1.0.1"

val jaicf = "0.4.3"
val slf4j = "1.7.30"
val ktor = "1.3.1"

application {
    mainClassName = "com.podkopaev.alexander.itsm.WebhookKt"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven(uri("https://jitpack.io"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation ("com.google.code.gson:gson:2.8.6")

    implementation("org.slf4j:slf4j-simple:$slf4j")
    implementation("org.slf4j:slf4j-log4j12:$slf4j")

    implementation("com.justai.jaicf:core:$jaicf")
    implementation("com.justai.jaicf:mongo:$jaicf")

    implementation("com.justai.jaicf:yandex-alice:$jaicf")
    implementation("com.justai.jaicf:telegram:$jaicf")

    implementation("io.ktor:ktor-server-netty:$ktor")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.create("stage") {
    dependsOn("shadowJar")
}
