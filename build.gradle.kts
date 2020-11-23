import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.36.0"
    kotlin("jvm") version "1.4.20"
    application
}

repositories {
    mavenCentral()
    jcenter()
    google()
    maven(url = "https://jitpack.io/")
}

dependencies {
    implementation(group = "com.github.Hexworks.amethyst", name = "amethyst.core-jvm", version = "2020.0.1-PREVIEW")
    implementation(group = "org.hexworks.cobalt", name = "cobalt.core-jvm", version = "2020.0.19-PREVIEW")
    implementation(group = "com.github.Hexworks.zircon", name = "zircon.core-jvm", version = "2020.1.9-PREVIEW")
    implementation(group = "com.github.Hexworks.zircon", name = "zircon.jvm.swing", version = "2020.1.9-PREVIEW")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.7.0")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.7.0")

    testImplementation(group = "org.mockito", name = "mockito-core", version = "3.6.0")
    testImplementation(group = "org.mockito", name = "mockito-junit-jupiter", version = "3.6.0")

    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.18.1")
}

application {
    mainClass.set("com.liquidforte.roguelike.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "14"
}