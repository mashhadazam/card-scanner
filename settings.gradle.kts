pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "card-scanner"
include(":app")
include(":core")

// One module per bank/provider. Card facts live in these modules;
// the app compiles them into the catalog it displays.
include(":banks:td")
include(":banks:rbc")
include(":banks:cibc")
include(":banks:bmo")
include(":banks:amex")
