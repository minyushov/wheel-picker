plugins {
  alias(libs.plugins.android.app)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.minyushov.wheel.sample"
  compileSdk = libs.versions.androidCompileSdk.get().toInt()
  defaultConfig {
    applicationId = namespace
    versionCode = 1
    versionName = "1.0"
    minSdk = libs.versions.androidMinSdk.get().toInt()
    targetSdk = libs.versions.androidTargetSdk.get().toInt()
  }
  compileOptions {
    sourceCompatibility(libs.versions.java.get())
    targetCompatibility(libs.versions.java.get())
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(project(":wheel-picker"))
}