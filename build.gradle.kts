// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}

buildscript {
    extra.apply {
        set("room_version", "2.5.0")
        set("lifecycle_version", "2.6.1")
    }
}