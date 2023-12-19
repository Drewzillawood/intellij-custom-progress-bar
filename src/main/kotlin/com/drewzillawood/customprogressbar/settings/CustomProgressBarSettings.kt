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
    var myPrimaryColor: Color = JBColor.GRAY
    var mySecondaryColor: Color = JBColor.lightGray
    var myPrimaryDemoColor: Color = JBColor.GRAY
    var mySecondaryDemoColor: Color = JBColor.lightGray

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