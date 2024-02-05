package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    var isCustomProgressBarEnabled = true

    var myIndeterminatePrimaryColor: Int = JBColor.GRAY.rgb
    var myIndeterminateSecondaryColor: Int = JBColor.lightGray.rgb
    var myIndeterminatePrimaryDemoColor: Int = JBColor.GRAY.rgb
    var myIndeterminateSecondaryDemoColor: Int = JBColor.lightGray.rgb

    var myDeterminatePrimaryColor: Int = JBColor.GRAY.rgb
    var myDeterminateSecondaryColor: Int = JBColor.lightGray.rgb
    var myDeterminatePrimaryDemoColor: Int = JBColor.GRAY.rgb
    var myDeterminateSecondaryDemoColor: Int = JBColor.lightGray.rgb

    var version: String = ""

    var cycleTime: Int = 800
    var cycleDemoTime: Int = 800
    var repaintInterval: Int = 50
    var repaintDemoInterval: Int = 50

    @Nullable
    override fun getState() = this

    override fun loadState(state: CustomProgressBarSettings) {
        XmlSerializerUtil.copyBean(state, this);
    }

    companion object {
        @JvmStatic
        fun getInstance() = service<CustomProgressBarSettings>()
    }
}