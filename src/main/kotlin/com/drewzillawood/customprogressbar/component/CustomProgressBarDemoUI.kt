package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color

class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.getInstance()

    override fun getIndeterminateSecondaryColor(): Color {
        return settings.myIndeterminateSecondaryDemoColor.color
    }

    override fun getIndeterminatePrimaryColor(): Color {
        return settings.myIndeterminatePrimaryDemoColor.color
    }

    override fun getDeterminatePrimaryColor(): Color {
        return settings.myDeterminatePrimaryDemoColor.color
    }

    override fun getDeterminateSecondaryColor(): Color {
        return settings.myDeterminateSecondaryDemoColor.color
    }
}