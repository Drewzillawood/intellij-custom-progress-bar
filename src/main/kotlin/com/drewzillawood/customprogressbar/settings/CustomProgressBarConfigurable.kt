package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.EventDispatcher
import java.awt.Color
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComponent
import kotlin.reflect.KFunction1

class CustomProgressBarConfigurable : SearchableConfigurable {

    private val myDispatcher = EventDispatcher.create(EventListener::class.java)

    private lateinit var panel: DialogPanel
    private lateinit var enabledCustomProgressBar : JCheckBox
    private lateinit var myPrimaryColorChooser : ColorPanel
    private lateinit var mySecondaryColorChooser : ColorPanel

    private var settings = CustomProgressBarSettings.instance;

    override fun createComponent(): JComponent {
        panel = panel {
            row {
                enabledCustomProgressBar = checkBox("Enable Custom Progress Bar:")
                    .bindSelected(settings::isCustomProgressBarEnabled)
                    .component
            }
            row("Primary Color:") {
                myPrimaryColorChooser = ColorPanel()
                cell(myPrimaryColorChooser)
                myPrimaryColorChooser.bindColor(
                    settings::myPrimaryColor,
                    settings::myPrimaryColor::set
                )
            }
            row("Secondary Color:") {
                mySecondaryColorChooser = ColorPanel()
                cell(mySecondaryColorChooser)
                mySecondaryColorChooser.bindColor(
                    settings::mySecondaryColor,
                    settings::mySecondaryColor::set
                )
            }
        }
        return panel;
    }

    override fun isModified(): Boolean {
        return myPrimaryColorChooser.selectedColor != settings.myPrimaryColor
                || mySecondaryColorChooser.selectedColor != settings.mySecondaryColor
                || panel.isModified()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = CustomProgressBarSettings.instance
        settings.isCustomProgressBarEnabled = enabledCustomProgressBar.isCursorSet
        settings.myPrimaryColor = myPrimaryColorChooser.selectedColor!!
        settings.mySecondaryColor = mySecondaryColorChooser.selectedColor!!
        panel.apply()
    }

    override fun getDisplayName(): String {
        return "Custom Progress Bar"
    }

    override fun getId(): String {
        return "preferences.custom.progress.bar"
    }
}

fun ColorPanel.bindColor(getter: () -> Color?, setter: KFunction1<Color, Unit>) {
    this.selectedColor = getter()
    selectedColor?.let { setter(it) }
}