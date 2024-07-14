package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.domain.GetDemoConfigUseCase
import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import com.intellij.openapi.components.service
import java.awt.Color
import javax.swing.UIManager

open class CustomProgressBarDemoUI : CustomProgressBarUI() {

    private val settings = CustomProgressBarSettings.getInstance()
    private var currentDemo = service<GetDemoConfigUseCase>().invoke()

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
        return Color(currentDemo.myIndeterminateSecondaryColor)
    }

    override fun getIndeterminatePrimaryColor(): Color {
        return Color(currentDemo.myIndeterminatePrimaryColor)
    }

    override fun getDeterminatePrimaryColor(): Color {
        return Color(currentDemo.myDeterminatePrimaryColor)
    }

    override fun getDeterminateSecondaryColor(): Color {
        return Color(currentDemo.myDeterminateSecondaryColor)
    }
}