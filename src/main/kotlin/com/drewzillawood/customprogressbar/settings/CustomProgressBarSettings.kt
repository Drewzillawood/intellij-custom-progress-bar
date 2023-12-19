package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable
import java.awt.Color

@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    var isCustomProgressBarEnabled = true

    var myIndeterminatePrimaryColor: Color = JBColor.GRAY
    var myIndeterminateSecondaryColor: Color = JBColor.lightGray
    var myIndeterminatePrimaryDemoColor: Color = JBColor.GRAY
    var myIndeterminateSecondaryDemoColor: Color = JBColor.lightGray

    var myDeterminatePrimaryColor: Color = JBColor.GRAY
    var myDeterminateSecondaryColor: Color = JBColor.lightGray
    var myDeterminatePrimaryDemoColor: Color = JBColor.GRAY
    var myDeterminateSecondaryDemoColor: Color = JBColor.lightGray

    @Nullable
    override fun getState() = this

    override fun loadState(state: CustomProgressBarSettings) {
        XmlSerializerUtil.copyBean(state, this);
    }

    companion object {
        val instance: CustomProgressBarSettings
            get() = ApplicationManager.getApplication().getService(CustomProgressBarSettings::class.java)
    }
}