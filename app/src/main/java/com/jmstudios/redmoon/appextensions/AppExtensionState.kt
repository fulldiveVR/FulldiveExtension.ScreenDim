package com.jmstudios.redmoon.appextensions

sealed class AppExtensionState(val id: String) {
    object START: AppExtensionState("start")
    object STOP: AppExtensionState("stop")
    object FAILURE: AppExtensionState("failure")
}

sealed class WorkType(val id: String) {
    object START: WorkType("start")
    object STOP: WorkType("stop")
    object OPEN: WorkType("open")
    object GetPermissionsRequired : WorkType("get_permissions_required")
    object GetStatus : WorkType("get_status")
}