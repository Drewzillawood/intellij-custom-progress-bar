package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable
import java.awt.Color

@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    private val DEFAULT_PRIMARY_BLUE = Color(64, 115, 213).rgb
    private val DEFAULT_SECONDARY_BLUE = Color(100, 148, 243).rgb

    var isCustomProgressBarEnabled: Boolean = true

    var myIndeterminatePrimaryColor: Int = DEFAULT_PRIMARY_BLUE
    var myIndeterminateSecondaryColor: Int = DEFAULT_SECONDARY_BLUE
    var myIndeterminatePrimaryDemoColor: Int = DEFAULT_PRIMARY_BLUE
    var myIndeterminateSecondaryDemoColor: Int = DEFAULT_SECONDARY_BLUE

    var myDeterminatePrimaryColor: Int = DEFAULT_PRIMARY_BLUE
    var myDeterminateSecondaryColor: Int = JBColor.GRAY.rgb
    var myDeterminatePrimaryDemoColor: Int = DEFAULT_PRIMARY_BLUE
    var myDeterminateSecondaryDemoColor: Int = JBColor.GRAY.rgb

    var version: String = ""

    var isAdvancedOptionsEnabled: Boolean = false
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