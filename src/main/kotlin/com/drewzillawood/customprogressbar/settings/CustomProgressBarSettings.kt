package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

@Service
@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    var isCustomProgressBarEnabled = true

    var myIndeterminatePrimaryColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    var myIndeterminateSecondaryColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)
    var myIndeterminatePrimaryDemoColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    var myIndeterminateSecondaryDemoColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)

    var myDeterminatePrimaryColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    var myDeterminateSecondaryColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)
    var myDeterminatePrimaryDemoColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    var myDeterminateSecondaryDemoColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)

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