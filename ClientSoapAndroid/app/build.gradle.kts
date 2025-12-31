plugins {
    id("com.android.application")
}

android {
    namespace = "ma.projet.clientsoap_android_ksoap"
    compileSdk = 34

    defaultConfig {
        applicationId = "ma.projet.clientsoap_android_ksoap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Bibliothèque SOAP - Base + J2SE (pour HttpTransportSE)
    implementation("com.github.simpligility.ksoap2-android:ksoap2-base:3.6.4") {
        exclude(group = "net.sourceforge.kobjects", module = "kobjects-j2me")
        exclude(group = "net.sourceforge.kxml", module = "kxml")
    }
    implementation("com.github.simpligility.ksoap2-android:ksoap2-j2se:3.6.4") {
        exclude(group = "net.sourceforge.kobjects", module = "kobjects-j2me")
        exclude(group = "net.sourceforge.kxml", module = "kxml")
    }

    // Dépendances XML
    implementation("net.sf.kxml:kxml2:2.3.0")
}