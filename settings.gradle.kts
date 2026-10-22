rootProject.name = "databridge"

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

include(
    "databridge-core",
    "databridge-engine",
    "databridge-connectors",
    "databridge-infrastructure",
    "databridge-server",
)
