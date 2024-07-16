package com.drewzillawood.customprogressbar.data.model

import com.intellij.openapi.components.BaseState
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PersistentConfigs(@Transient var map: MutableMap<String, Any> = defaultPersistentConfigsMap()) :
  BaseState() {
  var myIndeterminatePrimaryColor: Int by map
  var myIndeterminateSecondaryColor: Int by map
  var myDeterminatePrimaryColor: Int by map
  var myDeterminateSecondaryColor: Int by map
  var isAdvancedOptionsEnabled: Boolean by map
  var cycleTime: Int by map
  var repaintInterval: Int by map

  constructor(persistentConfigs: PersistentConfigs) : this() {
    myIndeterminatePrimaryColor = persistentConfigs.myIndeterminatePrimaryColor
    myIndeterminateSecondaryColor = persistentConfigs.myIndeterminateSecondaryColor
    myDeterminatePrimaryColor = persistentConfigs.myDeterminatePrimaryColor
    myDeterminateSecondaryColor = persistentConfigs.myDeterminateSecondaryColor
    isAdvancedOptionsEnabled = persistentConfigs.isAdvancedOptionsEnabled
    cycleTime = persistentConfigs.cycleTime
    repaintInterval = persistentConfigs.repaintInterval
  }
}