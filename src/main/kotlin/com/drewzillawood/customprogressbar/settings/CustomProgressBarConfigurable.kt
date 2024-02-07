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
                    .bindSelected(settings::isCustomProgressBarEnabled)
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
                                    { Color(settings.myIndeterminatePrimaryColor) },
                                    { settings.myIndeterminatePrimaryColor = it.rgb }
                                )
                                myIndeterminatePrimaryColorChooser.addActionListener {
                                    settings.myIndeterminatePrimaryDemoColor = myIndeterminatePrimaryColorChooser.selectedColor!!.rgb
                                }
                            }.resizableRow()
                            row("Secondary:") {
                                myIndeterminateSecondaryColorChooser = ColorPanel()
                                cell(myIndeterminateSecondaryColorChooser)
                                myIndeterminateSecondaryColorChooser.bindColor(
                                    { Color(settings.myIndeterminateSecondaryColor) },
                                    { settings.myIndeterminateSecondaryColor = it.rgb }
                                )
                                myIndeterminateSecondaryColorChooser.addActionListener {
                                    settings.myIndeterminateSecondaryDemoColor = myIndeterminateSecondaryColorChooser.selectedColor!!.rgb
                                }
                            }.resizableRow()
                        }
                        cell(indeterminateExampleProgressBar)
                    }
                    row {
                        advancedOptionsCheckBox = checkBox("Advanced")
                            .bindSelected(settings::isAdvancedOptionsEnabled)
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
                                    settings::cycleTime,
                                    settings::cycleTime::set
                                )
                                .component
                            cycleTimeSlider.addChangeListener {
                                settings.cycleDemoTime = cycleTimeSlider.value
                                indeterminateExampleProgressBar.setUI(CustomProgressBarDemoUI())
                            }
                        }
                        row("Repaint Interval (ms):") {
                            repaintIntervalSlider = slider(0, 200, 25, 50)
                                .bindValue(
                                    settings::repaintInterval,
                                    settings::repaintInterval::set
                                )
                                .component
                            repaintIntervalSlider.addChangeListener {
                                settings.repaintDemoInterval = repaintIntervalSlider.value
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
                                    { Color(settings.myDeterminatePrimaryColor) },
                                    { settings.myDeterminatePrimaryColor = it.rgb }
                                )
                                myDeterminatePrimaryColorChooser.addActionListener {
                                    settings.myDeterminatePrimaryDemoColor =
                                        myDeterminatePrimaryColorChooser.selectedColor!!.rgb
                                }
                            }
                            row("Secondary:") {
                                myDeterminateSecondaryColorChooser = ColorPanel()
                                cell(myDeterminateSecondaryColorChooser)
                                myDeterminateSecondaryColorChooser.bindColor(
                                    { Color(settings.myDeterminateSecondaryColor) },
                                    { settings.myDeterminateSecondaryColor = it.rgb }
                                )
                                myDeterminateSecondaryColorChooser.addActionListener {
                                    settings.myDeterminateSecondaryDemoColor =
                                        myDeterminateSecondaryColorChooser.selectedColor!!.rgb
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
        return myIndeterminatePrimaryColorChooser.selectedColor != Color(settings.myIndeterminatePrimaryColor)
                || myIndeterminateSecondaryColorChooser.selectedColor != Color(settings.myIndeterminateSecondaryColor)
                || myDeterminatePrimaryColorChooser.selectedColor != Color(settings.myDeterminatePrimaryColor)
                || myDeterminateSecondaryColorChooser.selectedColor != Color(settings.myDeterminateSecondaryColor)
                || advancedOptionsCheckBox.isSelected != settings.isAdvancedOptionsEnabled
                || cycleTimeSlider.value != settings.cycleTime
                || repaintIntervalSlider.value != settings.repaintInterval
                || panel.isModified()
    }

    override fun reset() {
        val settings = CustomProgressBarSettings.getInstance()
        settings.myIndeterminatePrimaryDemoColor = settings.myIndeterminatePrimaryColor
        settings.myIndeterminateSecondaryDemoColor = settings.myIndeterminateSecondaryColor
        settings.myDeterminatePrimaryDemoColor = settings.myDeterminatePrimaryColor
        settings.myDeterminateSecondaryDemoColor = settings.myDeterminateSecondaryColor
        settings.cycleDemoTime = settings.cycleTime
        settings.repaintDemoInterval = settings.repaintInterval

        enabledCustomProgressBar.isSelected = settings.isCustomProgressBarEnabled
        myIndeterminatePrimaryColorChooser.selectedColor = Color(settings.myIndeterminatePrimaryColor)
        myIndeterminateSecondaryColorChooser.selectedColor = Color(settings.myIndeterminateSecondaryColor)
        myDeterminatePrimaryColorChooser.selectedColor = Color(settings.myDeterminatePrimaryColor)
        myDeterminateSecondaryColorChooser.selectedColor = Color(settings.myDeterminateSecondaryColor)
        advancedOptionsCheckBox.isSelected = settings.isAdvancedOptionsEnabled
        cycleTimeSlider.value = settings.cycleTime
        repaintIntervalSlider.value = settings.repaintInterval
        super.reset()
    }

    override fun cancel() {
        reset()
        super.cancel()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = CustomProgressBarSettings.getInstance()
        settings.isCustomProgressBarEnabled = enabledCustomProgressBar.isSelected

        settings.myIndeterminatePrimaryColor = myIndeterminatePrimaryColorChooser.selectedColor!!.rgb
        settings.myIndeterminatePrimaryDemoColor = settings.myIndeterminatePrimaryColor
        settings.myIndeterminateSecondaryColor = myIndeterminateSecondaryColorChooser.selectedColor!!.rgb
        settings.myIndeterminateSecondaryDemoColor = settings.myIndeterminateSecondaryColor

        settings.isAdvancedOptionsEnabled = advancedOptionsCheckBox.isSelected
        settings.cycleTime = cycleTimeSlider.value
        settings.cycleDemoTime = settings.cycleTime
        settings.repaintInterval = repaintIntervalSlider.value
        settings.repaintDemoInterval = settings.repaintInterval

        settings.myDeterminatePrimaryColor = myDeterminatePrimaryColorChooser.selectedColor!!.rgb
        settings.myDeterminatePrimaryDemoColor = settings.myDeterminatePrimaryColor
        settings.myDeterminateSecondaryColor = myDeterminateSecondaryColorChooser.selectedColor!!.rgb
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

fun ColorPanel.bindColor(getter: () -> Color?, setter: (Color) -> Unit) {
    getter()?.let {
        this.selectedColor = it
        setter(it)
    }
}