package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color

class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.instance

    override fun getIndeterminateSecondaryColor(): Color {
        return settings.myIndeterminateSecondaryDemoColor
    }

    override fun getIndeterminatePrimaryColor(): Color {
        return settings.myIndeterminatePrimaryDemoColor
    }

    override fun getDeterminatePrimaryColor(): Color {
        return settings.myDeterminatePrimaryDemoColor
    }

    override fun getDeterminateSecondaryColor(): Color {
        return settings.myDeterminateSecondaryDemoColor
    }
}