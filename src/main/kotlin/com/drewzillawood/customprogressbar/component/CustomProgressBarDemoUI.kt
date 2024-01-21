package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color

class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.getInstance()

    override fun getIndeterminateSecondaryColor(): Color {
        return settings.myIndeterminateSecondaryDemoColor.getColor()
    }

    override fun getIndeterminatePrimaryColor(): Color {
        return settings.myIndeterminatePrimaryDemoColor.getColor()
    }

    override fun getDeterminatePrimaryColor(): Color {
        return settings.myDeterminatePrimaryDemoColor.getColor()
    }

    override fun getDeterminateSecondaryColor(): Color {
        return settings.myDeterminateSecondaryDemoColor.getColor()
    }
}