package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Property
import org.jetbrains.annotations.Nullable

@Service
@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    @Property var isCustomProgressBarEnabled = true

    @Property(surroundWithTag = false) var myIndeterminatePrimaryColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    @Property(surroundWithTag = false) var myIndeterminateSecondaryColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)
    @Property(surroundWithTag = false) var myIndeterminatePrimaryDemoColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    @Property(surroundWithTag = false) var myIndeterminateSecondaryDemoColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)

    @Property(surroundWithTag = false) var myDeterminatePrimaryColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    @Property(surroundWithTag = false) var myDeterminateSecondaryColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)
    @Property(surroundWithTag = false) var myDeterminatePrimaryDemoColor = CustomProgressBarColorSettings(JBColor.GRAY.rgb)
    @Property(surroundWithTag = false) var myDeterminateSecondaryDemoColor = CustomProgressBarColorSettings(JBColor.lightGray.rgb)

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