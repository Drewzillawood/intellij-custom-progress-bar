package com.drewzillawood.customprogressbar.listener

import com.drewzillawood.customprogressbar.CustomProgressBarUIJava
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.wm.IdeFrame
import javax.swing.UIManager

class CustomProgressBarListener : LafManagerListener, DynamicPluginListener, ApplicationActivationListener {

    private val PROGRESS_BAR_UI = "ProgressBarUI"
    private val CUSTOM_PROGRESS_BAR_UI_NAME = CustomProgressBarUIJava::class.java.name
    private lateinit var previousProgressBar: Object
    private lateinit var pluginId: PluginId

    override fun lookAndFeelChanged(source: LafManager) {
        updateProgressBarUi()
    }

    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        if (pluginId == pluginDescriptor.pluginId) {
            updateProgressBarUi()
        }
    }

    override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
        if (pluginId == pluginDescriptor.pluginId) {
            resetProgressBarUi()
        }
    }

    override fun applicationActivated(ideFrame: IdeFrame) {
        updateProgressBarUi()
    }

    private fun updateProgressBarUi() {
        UIManager.put(PROGRESS_BAR_UI, CUSTOM_PROGRESS_BAR_UI_NAME)
        UIManager.getDefaults()[CUSTOM_PROGRESS_BAR_UI_NAME] = CustomProgressBarUIJava::class.java
    }

    private fun resetProgressBarUi() {
        UIManager.put(PROGRESS_BAR_UI, previousProgressBar)
    }
}