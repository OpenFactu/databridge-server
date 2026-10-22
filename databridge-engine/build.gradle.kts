plugins {
    id("databridge.java-conventions")
}

description = "Runtime propio de flujos de DataBridge."

dependencies {
    api(project(":databridge-core"))
}
