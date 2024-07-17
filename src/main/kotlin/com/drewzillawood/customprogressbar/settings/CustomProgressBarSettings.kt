package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : SimplePersistentStateComponent<CustomProgressBarSettings.State>(State()) {
  class State : BaseState() {
    var version: String = ""
  }
}