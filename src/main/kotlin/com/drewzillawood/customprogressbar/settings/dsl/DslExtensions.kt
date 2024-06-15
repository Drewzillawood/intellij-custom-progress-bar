package com.drewzillawood.customprogressbar.settings.dsl

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import com.drewzillawood.customprogressbar.settings.bindColor
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.Row
import java.awt.Color

fun Row.indeterminatePrimaryColorChooser(
  settings: CustomProgressBarSettings,
  colorChooser: ColorPanel
) {
  cell(colorChooser)
  colorChooser.bindColor(
    { Color(settings.myIndeterminatePrimaryColor) },
    { settings.myIndeterminatePrimaryColor = it.rgb }
  )
  colorChooser.addActionListener {
    settings.myIndeterminatePrimaryDemoColor = colorChooser.selectedColor!!.rgb
  }
}

fun Row.indeterminateSecondaryColorChooser(
  settings: CustomProgressBarSettings,
  colorChooser: ColorPanel
) {
  cell(colorChooser)
  colorChooser.bindColor(
    { Color(settings.myIndeterminateSecondaryColor) },
    { it: Color -> settings.myIndeterminateSecondaryColor = it.rgb }
  )
  colorChooser.addActionListener {
    settings.myIndeterminateSecondaryDemoColor = colorChooser.selectedColor!!.rgb
  }
}

fun Row.determinatePrimaryColorChooser(
  settings: CustomProgressBarSettings,
  colorChooser: ColorPanel
) {
  cell(colorChooser)
  colorChooser.bindColor(
    { Color(settings.myDeterminatePrimaryColor) },
    { it: Color -> settings.myDeterminatePrimaryColor = it.rgb }
  )
  colorChooser.addActionListener {
    settings.myDeterminatePrimaryDemoColor = colorChooser.selectedColor!!.rgb
  }
}

fun Row.determinateSecondaryColorChooser(
  settings: CustomProgressBarSettings,
  colorChooser: ColorPanel
) {
  cell(colorChooser)
  colorChooser.bindColor(
    { Color(settings.myDeterminateSecondaryColor) },
    { it: Color -> settings.myDeterminateSecondaryColor = it.rgb }
  )
  colorChooser.addActionListener {
    settings.myDeterminateSecondaryDemoColor = colorChooser.selectedColor!!.rgb
  }
}