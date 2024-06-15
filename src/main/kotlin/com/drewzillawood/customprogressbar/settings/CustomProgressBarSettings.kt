package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor

@Service
@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : SimplePersistentStateComponent<CustomProgressBarSettings.State>(State()) {

    class State : BaseState() {
        var isCustomProgressBarEnabled: Boolean by property(true)

        var myIndeterminatePrimaryColor: Int by property(JBColor.GRAY.rgb)
        var myIndeterminateSecondaryColor: Int by property(JBColor.lightGray.rgb)
        var myIndeterminatePrimaryDemoColor: Int by property(JBColor.GRAY.rgb)
        var myIndeterminateSecondaryDemoColor: Int by property(JBColor.lightGray.rgb)

        var myDeterminatePrimaryColor: Int by property(JBColor.GRAY.rgb)
        var myDeterminateSecondaryColor: Int by property(JBColor.lightGray.rgb)
        var myDeterminatePrimaryDemoColor: Int by property(JBColor.GRAY.rgb)
        var myDeterminateSecondaryDemoColor: Int by property(JBColor.lightGray.rgb)

        var version: String? by string()

        var isAdvancedOptionsEnabled: Boolean by property(false)
        var cycleTime: Int by property(800)
        var cycleDemoTime: Int by property(800)
        var repaintInterval: Int by property(50)
        var repaintDemoInterval: Int by property(50)
    }

    companion object {
        @JvmStatic
        fun getInstance() = service<CustomProgressBarSettings>()
    }
}