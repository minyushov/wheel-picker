import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
  alias(libs.plugins.android.lib)
  alias(libs.plugins.maven.publish)
}

android {
  namespace = "com.minyushov.wheel"
  compileSdk = 36
  defaultConfig {
    minSdk = 24
  }
  compileOptions {
    sourceCompatibility(libs.versions.java.get())
    targetCompatibility(libs.versions.java.get())
  }
}

dependencies {
  implementation(libs.androidx.appcompat.resources)
}

mavenPublishing {
  coordinates("io.github.minyushov", "wheel-picker", "2.0.1")

  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
  signAllPublications()

  configure(
    AndroidSingleVariantLibrary(
      variant = "release",
      sourcesJar = true,
      publishJavadocJar = false
    )
  )

  pom {
    name = "Wheel Picker"
    description = "Simple wheel view for Android"
    url = "https://github.com/minyushov/wheel-picker"
    licenses {
      license {
        name = "The Apache License, Version 2.0"
        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
        distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
      }
    }
    developers {
      developer {
        id = "minyushov"
        name = "Semyon Minyushov"
        url = "minyushov@gmail.com"
      }
    }
    scm {
      url.set("https://github.com/minyushov/wheel-picker/")
      connection.set("scm:git:git://github.com/minyushov/wheel-picker.git")
      developerConnection.set("scm:git:ssh://git@github.com/minyushov/wheel-picker.git")
    }
  }
}