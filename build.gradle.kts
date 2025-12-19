import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    java
    `maven-publish`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

allprojects {
    group = "net.vxoidpvp.droplet"
    version = rootProject.findProperty("project.version") as String

    repositories {
        mavenCentral()
        maven("https://repo.simplecloud.app/snapshots")
        maven("https://buf.build/gen/maven")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlin.get().pluginId)
        plugin(rootProject.libs.plugins.shadow.get().pluginId)
        apply(plugin = "maven-publish")
    }

    dependencies {
        implementation(rootProject.libs.kotlin.stdlib)
        testImplementation(rootProject.libs.kotlin.test)
        implementation(rootProject.libs.kotlin.coroutines)
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_2_2)
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    tasks.named("build") {
        dependsOn("shadowJar")
    }

    tasks.named("shadowJar", ShadowJar::class.java) {
        mergeServiceFiles()
        archiveFileName.set("${project.name}.jar")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    tasks.register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
    }

}