package com.drewzillawood.customprogressbar.settings

import com.drewzillawood.customprogressbar.component.CustomProgressBarDemoUI
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.selected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.Color
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JProgressBar

class CustomProgressBarConfigurable : SearchableConfigurable, CoroutineScope {

    private val indeterminateExampleProgressBar = JProgressBar()
    private val determinateExampleProgressBar = JProgressBar()

    override val coroutineContext = CoroutineScope(Job()).coroutineContext

    private lateinit var panel: DialogPanel
    private lateinit var enabledCustomProgressBar : JCheckBox
    private lateinit var myIndeterminatePrimaryColorChooser : ColorPanel
    private lateinit var myIndeterminateSecondaryColorChooser : ColorPanel
    private lateinit var myDeterminatePrimaryColorChooser : ColorPanel
    private lateinit var myDeterminateSecondaryColorChooser : ColorPanel

    private var settings = CustomProgressBarSettings.getInstance();

    init {
        indeterminateExampleProgressBar.setUI(CustomProgressBarDemoUI())
        indeterminateExampleProgressBar.isIndeterminate = true

        determinateExampleProgressBar.setUI(CustomProgressBarDemoUI())
        determinateExampleProgressBar.isIndeterminate = false
        determinateExampleProgressBar.minimum = 0
        determinateExampleProgressBar.maximum = 100
        determinateExampleProgressBar.value = 0
    }

    override fun createComponent(): JComponent {
        panel = panel {
            row {
                enabledCustomProgressBar = checkBox("Enable Custom Progress Bar:")
                    .bindSelected(settings::isCustomProgressBarEnabled)
                    .component
            }
            group("Indeterminate") {
                twoColumnsRow(
                    {
                        panel {
                            row {
                                cell(indeterminateExampleProgressBar)
                            }
                        }
                    },
                    {
                        panel {
                            row("Primary:") {
                                myIndeterminatePrimaryColorChooser = ColorPanel()
                                cell(myIndeterminatePrimaryColorChooser)
                                myIndeterminatePrimaryColorChooser.bindColor(
                                    getter = { settings.myIndeterminatePrimaryColor.color },
                                    setter = { settings.myIndeterminatePrimaryColor.color = (it) }
                                )
                                myIndeterminatePrimaryColorChooser.addActionListener {
                                    settings.myIndeterminatePrimaryDemoColor.color = myIndeterminatePrimaryColorChooser.selectedColor!!
                                }
                            }
                            row("Secondary:") {
                                myIndeterminateSecondaryColorChooser = ColorPanel()
                                cell(myIndeterminateSecondaryColorChooser)
                                myIndeterminateSecondaryColorChooser.bindColor(
                                    getter = { settings.myIndeterminateSecondaryColor.color },
                                    setter = { settings.myIndeterminateSecondaryColor.color = (it) }
                                )
                                myIndeterminateSecondaryColorChooser.addActionListener {
                                    settings.myIndeterminateSecondaryDemoColor.color = myIndeterminateSecondaryColorChooser.selectedColor!!
                                }
                            }
                        }
                    }
                )
            }.visibleIf(enabledCustomProgressBar.selected)
            group("Determinate") {
                twoColumnsRow(
                    {
                        panel {
                            row {
                                cell(determinateExampleProgressBar)
                            }
                        }
                    },
                    {
                        panel {
                            row("Primary:") {
                                myDeterminatePrimaryColorChooser = ColorPanel()
                                cell(myDeterminatePrimaryColorChooser)
                                myDeterminatePrimaryColorChooser.bindColor(
                                    getter = { settings.myDeterminatePrimaryColor.color },
                                    setter = { settings.myDeterminatePrimaryColor.color = (it) }
                                )
                                myDeterminatePrimaryColorChooser.addActionListener {
                                    settings.myDeterminatePrimaryDemoColor.color = myDeterminatePrimaryColorChooser.selectedColor!!
                                }
                            }
                            row("Secondary:") {
                                myDeterminateSecondaryColorChooser = ColorPanel()
                                cell(myDeterminateSecondaryColorChooser)
                                myDeterminateSecondaryColorChooser.bindColor(
                                    getter = { settings.myDeterminateSecondaryColor.color },
                                    setter = { settings.myDeterminateSecondaryColor.color = (it) }
                                )
                                myDeterminateSecondaryColorChooser.addActionListener {
                                    settings.myDeterminateSecondaryDemoColor.color = myDeterminateSecondaryColorChooser.selectedColor!!
                                }
                            }
                        }
                    }
                )
            }.visibleIf(enabledCustomProgressBar.selected)
        }

        simulateProgress()

        return panel;
    }

    private fun simulateProgress() {
        val totalTime = 5000L
        val intervalTime = 100L

        val increment = (determinateExampleProgressBar.maximum * intervalTime / totalTime).toInt()

        launch {
            while (isActive)
                for (i in 1..(totalTime / intervalTime)) {
                    delay(intervalTime)
                    determinateExampleProgressBar.value = (determinateExampleProgressBar.value + increment) % determinateExampleProgressBar.maximum
                }
        }
    }

    override fun isModified(): Boolean {
        return myIndeterminatePrimaryColorChooser.selectedColor != settings.myIndeterminatePrimaryColor.color
                || myIndeterminateSecondaryColorChooser.selectedColor != settings.myIndeterminateSecondaryColor.color
                || myDeterminatePrimaryColorChooser.selectedColor != settings.myDeterminatePrimaryColor.color
                || myDeterminateSecondaryColorChooser.selectedColor != settings.myDeterminateSecondaryColor.color
                || panel.isModified()
    }

    override fun reset() {
        val settings = CustomProgressBarSettings.getInstance()
        settings.myIndeterminatePrimaryDemoColor = settings.myIndeterminatePrimaryColor
        settings.myIndeterminateSecondaryDemoColor = settings.myIndeterminateSecondaryColor
        settings.myDeterminatePrimaryDemoColor = settings.myDeterminatePrimaryColor
        settings.myDeterminateSecondaryDemoColor = settings.myDeterminateSecondaryColor

        myIndeterminatePrimaryColorChooser.selectedColor = settings.myIndeterminatePrimaryColor.color
        myIndeterminateSecondaryColorChooser.selectedColor = settings.myIndeterminateSecondaryColor.color
        myDeterminatePrimaryColorChooser.selectedColor = settings.myDeterminatePrimaryColor.color
        myDeterminateSecondaryColorChooser.selectedColor = settings.myDeterminateSecondaryColor.color
        super.reset()
    }

    override fun cancel() {
        reset()
        super.cancel()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = CustomProgressBarSettings.getInstance()
        settings.isCustomProgressBarEnabled = enabledCustomProgressBar.isCursorSet

        settings.myIndeterminatePrimaryColor.RGB = myIndeterminatePrimaryColorChooser.selectedColor!!.rgb
        settings.myIndeterminatePrimaryDemoColor.RGB = settings.myIndeterminatePrimaryColor.RGB
        settings.myIndeterminateSecondaryColor.RGB = myIndeterminateSecondaryColorChooser.selectedColor!!.rgb
        settings.myIndeterminateSecondaryDemoColor.RGB = settings.myIndeterminateSecondaryColor.RGB

        settings.myDeterminatePrimaryColor.RGB = myDeterminatePrimaryColorChooser.selectedColor!!.rgb
        settings.myDeterminatePrimaryDemoColor.RGB = settings.myDeterminatePrimaryColor.RGB
        settings.myDeterminateSecondaryColor.RGB = myDeterminateSecondaryColorChooser.selectedColor!!.rgb
        settings.myDeterminateSecondaryDemoColor.RGB = settings.myDeterminateSecondaryColor.RGB
        panel.apply()
    }

    override fun getDisplayName(): String {
        return "Custom Progress Bar"
    }

    override fun getId(): String {
        return "preferences.custom.progress.bar"
    }
}

fun ColorPanel.bindColor(getter: () -> Color?, setter: (Color) -> Unit) {
    getter()?.let {
        this.selectedColor = it
        setter(it)
    }
}