package com.drewzillawood.customprogressbar.listener

import com.drewzillawood.customprogressbar.CustomProgressBarUIJava
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import javax.swing.UIManager

class CustomProgressBarListener : LafManagerListener {

    override fun lookAndFeelChanged(source: LafManager) {
        updateProgressBarUi();
    }

    private fun updateProgressBarUi() {
        UIManager.put("ProgressBarUI", CustomProgressBarUIJava::class.java.name)
        UIManager.getDefaults()[CustomProgressBarUIJava::class.java.name] = CustomProgressBarUIJava::class.java
    }
}