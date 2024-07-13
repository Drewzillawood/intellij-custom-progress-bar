package com.drewzillawood.customprogressbar.data.model
import com.intellij.openapi.components.BaseState
import com.intellij.ui.JBColor
import java.awt.Color

private val DEFAULT_PRIMARY_BLUE = Color(64, 115, 213)
private val DEFAULT_SECONDARY_BLUE = Color(100, 148, 243)

data class PersistentConfigs(
  var myIndeterminatePrimaryColor: Int = DEFAULT_PRIMARY_BLUE.rgb,
  var myIndeterminateSecondaryColor: Int = DEFAULT_SECONDARY_BLUE.rgb,
  var myDeterminatePrimaryColor: Int = DEFAULT_PRIMARY_BLUE.rgb,
  var myDeterminateSecondaryColor: Int = JBColor.GRAY.rgb
): BaseState()
