plugins {
    id("databridge.java-conventions")
    alias(libs.plugins.spring.boot)
}

description = "Ensamblaje Spring Boot del backend de DataBridge."

dependencies {
    implementation(project(":databridge-core"))
    implementation(project(":databridge-engine"))
    implementation(project(":databridge-connectors"))
    implementation(project(":databridge-infrastructure"))
    implementation(libs.spring.boot.starter)
    testImplementation(libs.spring.boot.starter.test)
}
