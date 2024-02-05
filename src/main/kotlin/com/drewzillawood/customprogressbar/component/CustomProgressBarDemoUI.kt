package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import java.awt.Color
import javax.swing.UIManager

open class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.getInstance()

    override fun updateIndeterminateAnimationIndex(startMillis: Long) {
        val numFrames = settings.cycleDemoTime / settings.repaintDemoInterval
        val timePassed = System.currentTimeMillis() - startMillis
        this.animationIndex = (timePassed / settings.repaintDemoInterval.toLong() % numFrames.toLong()).toInt()
    }

    override fun installDefaults() {
        super.installDefaults()
        UIManager.put("ProgressBar.repaintInterval", settings.repaintDemoInterval)
        UIManager.put("ProgressBar.cycleTime", settings.cycleDemoTime)
    }

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