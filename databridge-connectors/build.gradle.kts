plugins {
    id("databridge.java-conventions")
}

description = "Conectores por protocolo/tecnología (JDBC a partir de la Fase 2)."

dependencies {
    api(project(":databridge-core"))
}
