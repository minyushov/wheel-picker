pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    google()
    mavenCentral()
  }
}

include(":wheel-picker")
include(":sample")
