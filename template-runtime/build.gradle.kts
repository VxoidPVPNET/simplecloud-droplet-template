plugins {
    application
    alias(libs.plugins.jooq.codegen)
}

dependencies {
    implementation(project(":template-shared"))
    implementation(libs.bundles.jooq)
    implementation(libs.postgresql.jdbc)
    implementation(libs.bundles.log4j)
    implementation(libs.clikt)

    jooqCodegen(libs.jooq.meta.extensions)
}

application {
    mainClass.set("net.vxoidpvp.template.runtime.launcher.LauncherKt")
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/source/db/main/java",
            )
        }
        resources {
            srcDirs(
                "src/main/db"
            )
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.jooqCodegen)
}