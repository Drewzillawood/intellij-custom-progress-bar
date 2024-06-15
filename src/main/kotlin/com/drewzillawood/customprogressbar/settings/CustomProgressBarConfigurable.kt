package com.drewzillawood.customprogressbar.settings

import com.drewzillawood.customprogressbar.component.CustomProgressBarDemoUI
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindValue
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
import javax.swing.JSlider

class CustomProgressBarConfigurable : SearchableConfigurable, CoroutineScope {

    private val CYCLE_TIME_DEFAULT = 800
    private val REPAINT_INTERVAL_DEFAULT = 50

    private val indeterminateExampleProgressBar = JProgressBar()
    private val determinateExampleProgressBar = JProgressBar()

    override val coroutineContext = CoroutineScope(Job()).coroutineContext

    private lateinit var panel: DialogPanel
    private lateinit var enabledCustomProgressBar: JCheckBox
    private lateinit var myIndeterminatePrimaryColorChooser: ColorPanel
    private lateinit var myIndeterminateSecondaryColorChooser: ColorPanel
    private lateinit var myDeterminatePrimaryColorChooser: ColorPanel
    private lateinit var myDeterminateSecondaryColorChooser: ColorPanel
    private lateinit var advancedOptionsCheckBox: JCheckBox
    private lateinit var cycleTimeSlider: JSlider
    private lateinit var repaintIntervalSlider: JSlider

    private var settings = CustomProgressBarSettings.getInstance()

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
                    .bindSelected(settings.state::isCustomProgressBarEnabled)
                    .component
                enabledCustomProgressBar.addChangeListener {
                    if (enabledCustomProgressBar.isSelected.not()) {
                        advancedOptionsCheckBox.isSelected = false
                    }
                }
            }
            group("Indeterminate") {
                panel {
                    row {
                        panel {
                            row("Primary:") {
                                myIndeterminatePrimaryColorChooser = ColorPanel()
                                cell(myIndeterminatePrimaryColorChooser)
                                myIndeterminatePrimaryColorChooser.bindColor(
                                    settings.state::myIndeterminatePrimaryColor,
                                    settings.state::myIndeterminatePrimaryColor::set
                                )
                                myIndeterminatePrimaryColorChooser.addActionListener {
                                    settings.state.myIndeterminatePrimaryDemoColor = myIndeterminatePrimaryColorChooser.selectedColor!!
                                }
                            }.resizableRow()
                            row("Secondary:") {
                                myIndeterminateSecondaryColorChooser = ColorPanel()
                                cell(myIndeterminateSecondaryColorChooser)
                                myIndeterminateSecondaryColorChooser.bindColor(
                                    settings.state::myIndeterminateSecondaryColor,
                                    settings.state::myIndeterminateSecondaryColor::set
                                )
                                myIndeterminateSecondaryColorChooser.addActionListener {
                                    settings.state.myIndeterminateSecondaryDemoColor = myIndeterminateSecondaryColorChooser.selectedColor!!
                                }
                            }.resizableRow()
                        }
                        cell(indeterminateExampleProgressBar)
                    }
                    row {
                        advancedOptionsCheckBox = checkBox("Advanced")
                            .bindSelected(settings.state::isAdvancedOptionsEnabled)
                            .component
                        advancedOptionsCheckBox.addChangeListener {
                            if (advancedOptionsCheckBox.isSelected.not()) {
                                cycleTimeSlider.value = CYCLE_TIME_DEFAULT
                                repaintIntervalSlider.value = REPAINT_INTERVAL_DEFAULT
                            }
                        }
                    }
                    indent {
                        row("Cycle Time (ms):") {
                            cycleTimeSlider = slider(0, 2000, 250, 500)
                                .bindValue(
                                    settings.state::cycleTime,
                                    settings.state::cycleTime::set
                                )
                                .component
                            cycleTimeSlider.addChangeListener {
                                settings.state.cycleDemoTime = cycleTimeSlider.value
                                indeterminateExampleProgressBar.setUI(CustomProgressBarDemoUI())
                            }
                        }
                        row("Repaint Interval (ms):") {
                            repaintIntervalSlider = slider(0, 200, 25, 50)
                                .bindValue(
                                    settings.state::repaintInterval,
                                    settings.state::repaintInterval::set
                                )
                                .component
                            repaintIntervalSlider.addChangeListener {
                                settings.state.repaintDemoInterval = repaintIntervalSlider.value
                                indeterminateExampleProgressBar.setUI(CustomProgressBarDemoUI())
                            }
                        }
                    }.visibleIf(advancedOptionsCheckBox.selected)
                }
            }.visibleIf(enabledCustomProgressBar.selected)
            group("Determinate") {
                panel {
                    row {
                        panel {
                            row("Primary:") {
                                myDeterminatePrimaryColorChooser = ColorPanel()
                                cell(myDeterminatePrimaryColorChooser)
                                myDeterminatePrimaryColorChooser.bindColor(
                                    settings.state::myDeterminatePrimaryColor,
                                    settings.state::myDeterminatePrimaryColor::set
                                )
                                myDeterminatePrimaryColorChooser.addActionListener {
                                    settings.state.myDeterminatePrimaryDemoColor =
                                        myDeterminatePrimaryColorChooser.selectedColor!!
                                }
                            }
                            row("Secondary:") {
                                myDeterminateSecondaryColorChooser = ColorPanel()
                                cell(myDeterminateSecondaryColorChooser)
                                myDeterminateSecondaryColorChooser.bindColor(
                                    settings.state::myDeterminateSecondaryColor,
                                    settings.state::myDeterminateSecondaryColor::set
                                )
                                myDeterminateSecondaryColorChooser.addActionListener {
                                    settings.state.myDeterminateSecondaryDemoColor =
                                        myDeterminateSecondaryColorChooser.selectedColor!!
                                }
                            }
                        }
                        cell(determinateExampleProgressBar)
                    }
                }
            }.visibleIf(enabledCustomProgressBar.selected)
        }

        simulateProgress()

        return panel;
    }

    private fun simulateProgress() {
        val totalTime = 2000L
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
        return myIndeterminatePrimaryColorChooser.selectedColor != settings.state.myIndeterminatePrimaryColor
                || myIndeterminateSecondaryColorChooser.selectedColor != settings.state.myIndeterminateSecondaryColor
                || myDeterminatePrimaryColorChooser.selectedColor != settings.state.myDeterminatePrimaryColor
                || myDeterminateSecondaryColorChooser.selectedColor != settings.state.myDeterminateSecondaryColor
                || advancedOptionsCheckBox.isSelected != settings.state.isAdvancedOptionsEnabled
                || cycleTimeSlider.value != settings.state.cycleTime
                || repaintIntervalSlider.value != settings.state.repaintInterval
                || panel.isModified()
    }

    override fun reset() {
        val settings = CustomProgressBarSettings.getInstance()
        settings.state.myIndeterminatePrimaryDemoColor = settings.state.myIndeterminatePrimaryColor
        settings.state.myIndeterminateSecondaryDemoColor = settings.state.myIndeterminateSecondaryColor
        settings.state.myDeterminatePrimaryDemoColor = settings.state.myDeterminatePrimaryColor
        settings.state.myDeterminateSecondaryDemoColor = settings.state.myDeterminateSecondaryColor
        settings.state.cycleDemoTime = settings.state.cycleTime
        settings.state.repaintDemoInterval = settings.state.repaintInterval

        enabledCustomProgressBar.isSelected = settings.state.isCustomProgressBarEnabled
        myIndeterminatePrimaryColorChooser.selectedColor = settings.state.myIndeterminatePrimaryColor
        myIndeterminateSecondaryColorChooser.selectedColor = settings.state.myIndeterminateSecondaryColor
        myDeterminatePrimaryColorChooser.selectedColor = settings.state.myDeterminatePrimaryColor
        myDeterminateSecondaryColorChooser.selectedColor = settings.state.myDeterminateSecondaryColor
        advancedOptionsCheckBox.isSelected = settings.state.isAdvancedOptionsEnabled
        cycleTimeSlider.value = settings.state.cycleTime
        repaintIntervalSlider.value = settings.state.repaintInterval
        super.reset()
    }

    override fun cancel() {
        reset()
        super.cancel()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = CustomProgressBarSettings.getInstance()
        settings.state.isCustomProgressBarEnabled = enabledCustomProgressBar.isSelected

        settings.state.myIndeterminatePrimaryColor = myIndeterminatePrimaryColorChooser.selectedColor!!
        settings.state.myIndeterminatePrimaryDemoColor = settings.state.myIndeterminatePrimaryColor
        settings.state.myIndeterminateSecondaryColor = myIndeterminateSecondaryColorChooser.selectedColor!!
        settings.state.myIndeterminateSecondaryDemoColor = settings.state.myIndeterminateSecondaryColor

        settings.state.isAdvancedOptionsEnabled = advancedOptionsCheckBox.isSelected
        settings.state.cycleTime = cycleTimeSlider.value
        settings.state.cycleDemoTime = settings.state.cycleTime
        settings.state.repaintInterval = repaintIntervalSlider.value
        settings.state.repaintDemoInterval = settings.state.repaintInterval

        settings.state.myDeterminatePrimaryColor = myDeterminatePrimaryColorChooser.selectedColor!!
        settings.state.myDeterminatePrimaryDemoColor = settings.state.myDeterminatePrimaryColor
        settings.state.myDeterminateSecondaryColor = myDeterminateSecondaryColorChooser.selectedColor!!
        settings.state.myDeterminateSecondaryDemoColor = settings.state.myDeterminateSecondaryColor
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