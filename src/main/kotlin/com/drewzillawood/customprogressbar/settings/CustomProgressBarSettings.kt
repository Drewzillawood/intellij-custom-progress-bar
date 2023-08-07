package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

@State(name = "CustomProgressBarSettings", storages = [(Storage("custom_progress_bar.xml"))])
class CustomProgressBarSettings : PersistentStateComponent<CustomProgressBarSettings> {

    var isCustomBarEnabled = true

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