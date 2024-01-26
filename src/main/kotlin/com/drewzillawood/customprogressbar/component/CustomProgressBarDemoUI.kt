package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color

class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.getInstance()

    override fun getIndeterminateSecondaryColor(): Color {
        return Color(settings.myIndeterminateSecondaryDemoColor)
    }

    override fun getIndeterminatePrimaryColor(): Color {
        return Color(settings.myIndeterminatePrimaryDemoColor)
    }

    override fun getDeterminatePrimaryColor(): Color {
        return Color(settings.myDeterminatePrimaryDemoColor)
    }

    override fun getDeterminateSecondaryColor(): Color {
        return Color(settings.myDeterminateSecondaryDemoColor)
    }
}