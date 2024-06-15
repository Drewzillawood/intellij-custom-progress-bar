package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color
import javax.swing.UIManager

open class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.getInstance()

    override fun updateIndeterminateAnimationIndex(startMillis: Long) {
        val numFrames = settings.state.cycleDemoTime / settings.state.repaintDemoInterval
        val timePassed = System.currentTimeMillis() - startMillis
        this.animationIndex = (timePassed / settings.state.repaintDemoInterval.toLong() % numFrames.toLong()).toInt()
    }

    override fun installDefaults() {
        super.installDefaults()
        UIManager.put("ProgressBar.repaintInterval", settings.state.repaintDemoInterval)
        UIManager.put("ProgressBar.cycleTime", settings.state.cycleDemoTime)
    }

    override fun getIndeterminateSecondaryColor(): Color {
        return Color(settings.state.myIndeterminateSecondaryDemoColor)
    }

    override fun getIndeterminatePrimaryColor(): Color {
        return Color(settings.state.myIndeterminatePrimaryDemoColor)
    }

    override fun getDeterminatePrimaryColor(): Color {
        return Color(settings.state.myDeterminatePrimaryDemoColor)
    }

    override fun getDeterminateSecondaryColor(): Color {
        return Color(settings.state.myDeterminateSecondaryDemoColor)
    }
}