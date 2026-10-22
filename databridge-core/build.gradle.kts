plugins {
    id("databridge.java-conventions")
}

description = "Contratos y modelos de DataBridge (sin Spring ni conectores concretos)."

dependencies {
    // Jackson y JSON Schema solo se usan en el paquete core.json para leer definiciones.
    implementation(libs.jackson.databind)
    implementation(libs.json.schema.validator)
}
