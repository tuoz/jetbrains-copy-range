plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.2"
    id("org.jetbrains.changelog") version "2.2.0"
}

fun prop(key: String): String = project.property(key) as String

group = prop("pluginGroup")
version = prop("pluginVersion")

repositories {
    mavenCentral()
}

intellij {
    pluginName.set(prop("pluginName"))
    version.set(prop("platformVersion"))
    type.set(prop("platformType"))
    downloadSources.set(prop("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(false)
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set(prop("pluginSinceBuild"))
    }

    runIde {
        ideDir.set(file("/Applications/IntelliJ IDEA.app/Contents"))
    }
}
