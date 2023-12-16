package com.drewzillawood.customprogressbar.settings.form

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import com.intellij.application.options.colors.ColorAndFontSettingsListener
import com.intellij.ui.ColorPanel
import com.intellij.util.EventDispatcher
import java.awt.event.ActionListener
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class CustomProgressBarPanel {

    private lateinit var panel: JPanel

    private lateinit var colorLabel1: JLabel

    private val colorLabels: List<JLabel> = arrayListOf(colorLabel1)

    private lateinit var color1: ColorPanel

    private val colors: List<ColorPanel> = arrayListOf(color1)

    private val eventDispatcher: EventDispatcher<ColorAndFontSettingsListener> =
        EventDispatcher.create(ColorAndFontSettingsListener::class.java)

    private val settings: CustomProgressBarSettings = CustomProgressBarSettings.instance

    fun component(): JComponent = panel

    init {
        val actionListener = ActionListener {
            eventDispatcher.multicaster.settingsChanged()
        }
        colors.forEach { it.addActionListener(actionListener) }
    }
}