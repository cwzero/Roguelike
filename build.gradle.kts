import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version "0.36.0"
    kotlin("jvm") version "1.4.21"
    application
}

repositories {
    mavenCentral()
    jcenter()
    google()
    maven(url = "https://jitpack.io/")
}

dependencies {
    implementation(group = "org.hexworks.amethyst", name = "amethyst.core-jvm", version = "2020.0.1-PREVIEW")
    implementation(group = "org.hexworks.cobalt", name = "cobalt.core-jvm", version = "2020.0.19-PREVIEW")
    implementation(group = "org.hexworks.zircon", name = "zircon.core-jvm", version = "2020.1.6-HOTFIX")
    implementation(group = "org.hexworks.zircon", name = "zircon.jvm.swing", version = "2020.1.6-HOTFIX")

    implementation(group = "com.github.kittinunf.result", name = "result", version = "3.1.0")
    implementation(group = "com.github.kittinunf.result", name = "result-coroutines", version = "3.1.0")

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = "5.7.0")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.7.0")

    testImplementation(group = "org.mockito", name = "mockito-core", version = "3.6.28")
    testImplementation(group = "org.mockito", name = "mockito-junit-jupiter", version = "3.6.28")

    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.18.1")
}

application {
    mainClass.set("com.liquidforte.roguelike.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        //freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}