plugins {
  alias(libs.plugins.android.app) apply false
  alias(libs.plugins.android.lib) apply false
  alias(libs.plugins.kotlin.android) apply false
}

tasks.register<Delete>("clean") {
  delete(rootProject.layout.buildDirectory)
}