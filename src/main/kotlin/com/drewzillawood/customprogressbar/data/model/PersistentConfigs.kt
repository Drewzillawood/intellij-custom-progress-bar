package com.drewzillawood.customprogressbar.data.model

import com.intellij.openapi.components.BaseState
import com.intellij.ui.JBColor
import kotlinx.serialization.Serializable
import java.awt.Color

private const val CYCLE_TIME_DEFAULT: Int = 800
private const val REPAINT_INTERVAL_DEFAULT: Int = 50

private val DEFAULT_PRIMARY_BLUE = Color(64, 115, 213)
private val DEFAULT_SECONDARY_BLUE = Color(100, 148, 243)

@Serializable
class PersistentConfigs() : BaseState() {
  var myIndeterminatePrimaryColor: Int by property(DEFAULT_PRIMARY_BLUE.rgb)
  var myIndeterminateSecondaryColor: Int by property(DEFAULT_SECONDARY_BLUE.rgb)
  var myDeterminatePrimaryColor: Int by property(DEFAULT_PRIMARY_BLUE.rgb)
  var myDeterminateSecondaryColor: Int by property(JBColor.GRAY.rgb)
  var isAdvancedOptionsEnabled: Boolean by property(false)
  var cycleTime: Int by property(CYCLE_TIME_DEFAULT)
  var repaintInterval: Int by property(REPAINT_INTERVAL_DEFAULT)

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