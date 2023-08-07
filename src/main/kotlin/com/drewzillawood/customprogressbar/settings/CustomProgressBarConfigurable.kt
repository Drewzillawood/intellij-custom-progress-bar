package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import javax.swing.JCheckBox
import javax.swing.JComponent

class CustomProgressBarConfigurable : SearchableConfigurable {
    private lateinit var panel: DialogPanel

    private var isCustomProgressBarEnabled = true

    override fun createComponent(): JComponent {
        panel = panel {
            lateinit var myFirstCheckBox: JCheckBox
            row {
                myFirstCheckBox = checkBox("Hello world: ")
                    .bindSelected(::isCustomProgressBarEnabled)
                    .component
            }
        }
        return panel;
    }

    override fun isModified(): Boolean {
        return panel.isModified()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        panel.apply()
    }

    override fun getDisplayName(): String {
        return "Custom Progress Bar"
    }

    override fun getId(): String {
        return "preferences.custom.progress.bar"
    }
}