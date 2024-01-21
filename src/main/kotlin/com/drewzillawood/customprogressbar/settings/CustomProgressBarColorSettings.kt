package com.drewzillawood.customprogressbar.settings

import com.intellij.ui.JBColor
import java.awt.Color

data class CustomProgressBarColorSettings(
    var RGB: Int = JBColor.GRAY.rgb
) {
    var color: Color
        get() = Color(RGB)
        set(value) {
            RGB = value.rgb
        }
}
