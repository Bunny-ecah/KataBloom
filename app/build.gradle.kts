import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

// Release signing: CI reads GitHub secrets; locally, put the same values in keystore.properties (git-ignored).
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun signingValue(key: String, env: String): String? =
    System.getenv(env)?.takeIf { it.isNotBlank() } ?: keystoreProps.getProperty(key)
val releaseStoreFile = signingValue("storeFile", "KATABLOOM_KEYSTORE_PATH")

android {
    namespace = "com.example.myproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myproject"
        minSdk = 23
        targetSdk = 36
        versionCode = 6
        versionName = "2.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = file(releaseStoreFile)
                storePassword = signingValue("storePassword", "KATABLOOM_KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "KATABLOOM_KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "KATABLOOM_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        create("qa") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".qa"
            matchingFallbacks += listOf("debug")
        }
        release {
            signingConfigs.findByName("release")?.let { signingConfig = it }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    testBuildType = "qa"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// QA has a separate package and no live Firebase connection or real learner data.
tasks.configureEach {
    if (name == "processQaGoogleServices") enabled = false
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

}
