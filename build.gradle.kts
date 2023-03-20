plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    kotlin("jvm") version "1.8.0"
}

dependencies {
    testImplementation("com.twelvemonkeys.imageio:imageio-webp:3.9.3")
    testImplementation("com.twelvemonkeys.imageio:imageio-jpeg:3.9.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
}

repositories {
    mavenCentral()
}

val compileJavaTask = tasks.named("compileJava")
compileJavaTask.configure {
    dependsOn("processResources")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}


kotlin {
}

repositories {
    mavenLocal()
    mavenCentral()
}


apply(from = "publishing.gradle")