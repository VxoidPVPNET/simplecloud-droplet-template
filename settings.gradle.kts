plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "simplecloud-droplet-template"
include("template-shared")
include("template-runtime")
include("template-api")
