package com.drewzillawood.customprogressbar.notification

import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationGroup

const val CUSTOM_PROGRESS_BAR_UPDATED = "Custom Progress Bar updated"

object DoNotAskService {

    fun canShowNotification(): Boolean {
        val id = "Notification.DoNotAsk-$CUSTOM_PROGRESS_BAR_UPDATED"
        val doNotAsk = PropertiesComponent.getInstance().getBoolean(id, false)

        return !doNotAsk
    }

    fun setDoNotAskFor(doNotAsk: Boolean) {
        var title = NotificationGroup.getGroupTitle("Notification.DoNotAsk-$CUSTOM_PROGRESS_BAR_UPDATED")
        if (title == null) {
            title = CUSTOM_PROGRESS_BAR_UPDATED
        }
        val manager = PropertiesComponent.getInstance()
        manager.setValue("Notification.DoNotAsk-$CUSTOM_PROGRESS_BAR_UPDATED", doNotAsk)
        manager.setValue("Notification.DisplayName-DoNotAsk-$CUSTOM_PROGRESS_BAR_UPDATED", title)
    }
}