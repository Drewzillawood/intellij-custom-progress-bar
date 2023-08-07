package com.drewzillawood.customprogressbar.listener

import com.drewzillawood.customprogressbar.component.CustomProgressBarUI
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import javax.swing.UIManager

class CustomProgressBarListener : LafManagerListener {

    override fun lookAndFeelChanged(source: LafManager) {
        updateProgressBarUi();
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBarUI", CustomProgressBarUI::class.java.name)
    }
}