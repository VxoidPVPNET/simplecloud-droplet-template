dependencies {
    api(libs.bundles.grpc)
    api(libs.bundles.configurate)
    api(libs.bundles.simplecloud) {
        exclude("org.slf4j")
        exclude("org.apache.logging")
        exclude("io.netty")
        exclude("io.grpc")
    }
}