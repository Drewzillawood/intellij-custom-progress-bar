package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color
import javax.swing.JComponent

class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.instance

    override fun getRemainderColor(): Color {
        return settings.mySecondaryDemoColor
    }

    override fun getFinishedColor(c: JComponent?): Color {
        return settings.myPrimaryDemoColor
    }
}