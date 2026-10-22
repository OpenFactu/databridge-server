plugins {
    id("databridge.java-conventions")
}

description = "Implementaciones técnicas: secretos, almacenamiento, logs."

dependencies {
    api(project(":databridge-core"))
    implementation(libs.slf4j.api)
}
