package com.drewzillawood.customprogressbar.data.model

import com.intellij.ui.JBColor
import java.awt.Color

private const val CYCLE_TIME_DEFAULT: Int = 800
private const val REPAINT_INTERVAL_DEFAULT: Int = 50

private val DEFAULT_PRIMARY_BLUE = Color(64, 115, 213)
private val DEFAULT_SECONDARY_BLUE = Color(100, 148, 243)

fun defaultPersistentConfigsMap(): MutableMap<String, Any> = mutableMapOf(
  "myIndeterminatePrimaryColor" to DEFAULT_PRIMARY_BLUE.rgb,
  "myIndeterminateSecondaryColor" to DEFAULT_SECONDARY_BLUE.rgb,
  "myDeterminatePrimaryColor" to DEFAULT_PRIMARY_BLUE.rgb,
  "myDeterminateSecondaryColor" to JBColor.GRAY.rgb,
  "isAdvancedOptionsEnabled" to false,
  "cycleTime" to CYCLE_TIME_DEFAULT,
  "repaintInterval" to REPAINT_INTERVAL_DEFAULT
)