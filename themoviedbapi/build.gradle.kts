plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.itsmcodez.themoviedbapi"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("src/main/keepRules/rules.keep")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)


    implementation(libs.commons.lang3)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(platform(libs.slf4j.bom))
    implementation(libs.slf4j.api)

    testImplementation(libs.junit)
}
