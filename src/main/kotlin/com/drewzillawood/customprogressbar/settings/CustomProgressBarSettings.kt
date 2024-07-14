package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    var isCustomProgressBarEnabled: Boolean = true

    var version: String = ""

    var isAdvancedOptionsEnabled: Boolean = false
    var cycleTime: Int = 800
    var repaintInterval: Int = 50

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