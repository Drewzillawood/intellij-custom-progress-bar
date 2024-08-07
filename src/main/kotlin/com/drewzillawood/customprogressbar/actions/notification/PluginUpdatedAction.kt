package com.drewzillawood.customprogressbar.actions.notification

import com.drewzillawood.customprogressbar.actions.notification.DoNotAskService.canShowNotification
import com.drewzillawood.customprogressbar.actions.notification.DoNotAskService.setDoNotAskFor
import com.drewzillawood.customprogressbar.settings.CustomProgressBarConfigurable
import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity.DumbAware
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.LayeredIcon

class PluginUpdatedAction : DumbAware {

  override fun runActivity(project: Project) {
    val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId("com.drewzillawood.CustomProgressBar"))
    val customProgressBarState = service<CustomProgressBarSettings>().state
    if (pluginDescriptor != null) {
      val installedVersion = pluginDescriptor.version
      val storedVersion = customProgressBarState.version
      if (!installedVersion.equals(storedVersion)) {
        customProgressBarState.version = installedVersion

        displayUpdateNotification(project, installedVersion)
      }
    }
  }

  private fun displayUpdateNotification(project: Project, version: String) {
    if (!canShowNotification()) {
      return
    }

    val notification = NotificationGroupManager.getInstance()
      .getNotificationGroup(CUSTOM_PROGRESS_BAR_UPDATED)
      .createNotification(
        "Custom Progress Bar",
        "Version: $version",
        NotificationType.INFORMATION,
      )
    val originalIcon = IconLoader.getIcon("/META-INF/pluginIcon.svg", javaClass.classLoader)
    val scaledIcon = LayeredIcon.layeredIcon(arrayOf(originalIcon)).scale(48.0F / originalIcon.iconWidth)
    notification.icon = scaledIcon
    notification.addAction(object :
      DumbAwareAction("Configure...") {
      override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, CustomProgressBarConfigurable::class.java)
      }
    })
    notification.addAction(object :
      DumbAwareAction("Don't show again") {
      override fun actionPerformed(e: AnActionEvent) {
        setDoNotAskFor(true)
        notification.hideBalloon()
      }
    })
    notification.notify(project)
  }
}