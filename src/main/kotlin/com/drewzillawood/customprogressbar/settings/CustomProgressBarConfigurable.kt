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
import kotlin.reflect.KFunction1

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

    private var settings = CustomProgressBarSettings.instance;

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
                                    settings::myIndeterminatePrimaryColor,
                                    settings::myIndeterminatePrimaryColor::set
                                )
                                myIndeterminatePrimaryColorChooser.addActionListener {
                                    settings.myIndeterminatePrimaryDemoColor = myIndeterminatePrimaryColorChooser.selectedColor!!
                                }
                            }
                            row("Secondary:") {
                                myIndeterminateSecondaryColorChooser = ColorPanel()
                                cell(myIndeterminateSecondaryColorChooser)
                                myIndeterminateSecondaryColorChooser.bindColor(
                                    settings::myIndeterminateSecondaryColor,
                                    settings::myIndeterminateSecondaryColor::set
                                )
                                myIndeterminateSecondaryColorChooser.addActionListener {
                                    settings.myIndeterminateSecondaryDemoColor = myIndeterminateSecondaryColorChooser.selectedColor!!
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
                                    settings::myDeterminatePrimaryColor,
                                    settings::myDeterminatePrimaryColor::set
                                )
                                myDeterminatePrimaryColorChooser.addActionListener {
                                    settings.myDeterminatePrimaryDemoColor = myDeterminatePrimaryColorChooser.selectedColor!!
                                }
                            }
                            row("Secondary:") {
                                myDeterminateSecondaryColorChooser = ColorPanel()
                                cell(myDeterminateSecondaryColorChooser)
                                myDeterminateSecondaryColorChooser.bindColor(
                                    settings::myDeterminateSecondaryColor,
                                    settings::myDeterminateSecondaryColor::set
                                )
                                myDeterminateSecondaryColorChooser.addActionListener {
                                    settings.myDeterminateSecondaryDemoColor = myDeterminateSecondaryColorChooser.selectedColor!!
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
        return myIndeterminatePrimaryColorChooser.selectedColor != settings.myIndeterminatePrimaryColor
                || myIndeterminateSecondaryColorChooser.selectedColor != settings.myIndeterminateSecondaryColor
                || myDeterminatePrimaryColorChooser.selectedColor != settings.myDeterminatePrimaryColor
                || myDeterminateSecondaryColorChooser.selectedColor != settings.myDeterminateSecondaryColor
                || panel.isModified()
    }

    override fun reset() {
        val settings = CustomProgressBarSettings.instance
        settings.myIndeterminatePrimaryDemoColor = settings.myIndeterminatePrimaryColor
        settings.myIndeterminateSecondaryDemoColor = settings.myIndeterminateSecondaryColor
        settings.myDeterminatePrimaryDemoColor = settings.myDeterminatePrimaryColor
        settings.myDeterminateSecondaryDemoColor = settings.myDeterminateSecondaryColor

        myIndeterminatePrimaryColorChooser.selectedColor = settings.myIndeterminatePrimaryColor
        myIndeterminateSecondaryColorChooser.selectedColor = settings.myIndeterminateSecondaryColor
        myDeterminatePrimaryColorChooser.selectedColor = settings.myDeterminatePrimaryColor
        myDeterminateSecondaryColorChooser.selectedColor = settings.myDeterminateSecondaryColor
        super.reset()
    }

    override fun cancel() {
        reset()
        super.cancel()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = CustomProgressBarSettings.instance
        settings.isCustomProgressBarEnabled = enabledCustomProgressBar.isCursorSet

        settings.myIndeterminatePrimaryColor = myIndeterminatePrimaryColorChooser.selectedColor!!
        settings.myIndeterminatePrimaryDemoColor = settings.myIndeterminatePrimaryColor
        settings.myIndeterminateSecondaryColor = myIndeterminateSecondaryColorChooser.selectedColor!!
        settings.myIndeterminateSecondaryDemoColor = settings.myIndeterminateSecondaryColor

        settings.myDeterminatePrimaryColor = myDeterminatePrimaryColorChooser.selectedColor!!
        settings.myDeterminatePrimaryDemoColor = settings.myDeterminatePrimaryColor
        settings.myDeterminateSecondaryColor = myDeterminateSecondaryColorChooser.selectedColor!!
        settings.myDeterminateSecondaryDemoColor = settings.myDeterminateSecondaryColor
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