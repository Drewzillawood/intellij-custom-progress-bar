package com.drewzillawood.customprogressbar.settings

import com.intellij.util.xmlb.annotations.Property
import java.awt.Color

class CustomProgressBarColorSettings {

    @Property
    var colorRGB: Int

    constructor(_color: Color) {
        this.colorRGB = _color.rgb
    }

    constructor(rgb: Int) {
        this.colorRGB = rgb
    }

    constructor() {
        this.colorRGB = Color.BLACK.rgb
    }

    fun getColor(): Color {
        return Color(colorRGB)
    }
}
