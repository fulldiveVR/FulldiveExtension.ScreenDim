package com.jmstudios.redmoon.appextensions

sealed class AppExtensionState(val id: String) {
    object START: AppExtensionState("START")
    object STOP: AppExtensionState("STOP")
    object FAILURE: AppExtensionState("FAILURE")
}

sealed class WorkType(val id: String) {
    object START: WorkType("START")
    object STOP: WorkType("STOP")
    object OPEN: WorkType("OPEN")
}