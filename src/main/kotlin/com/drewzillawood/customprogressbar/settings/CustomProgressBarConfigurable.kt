package com.drewzillawood.customprogressbar.settings

import com.drewzillawood.customprogressbar.component.CustomProgressBarDemoUI
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.Color
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JProgressBar
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KFunction1

class CustomProgressBarConfigurable : SearchableConfigurable, CoroutineScope {

    private val indeterminateExampleProgressBar = JProgressBar()
    private val determinateExampleProgressBar = JProgressBar()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var panel: DialogPanel
    private lateinit var enabledCustomProgressBar : JCheckBox
    private lateinit var myPrimaryColorChooser : ColorPanel
    private lateinit var mySecondaryColorChooser : ColorPanel

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
            row("Primary Color:") {
                myPrimaryColorChooser = ColorPanel()
                cell(myPrimaryColorChooser)
                myPrimaryColorChooser.bindColor(
                    settings::myPrimaryColor,
                    settings::myPrimaryColor::set
                )
                myPrimaryColorChooser.addActionListener {
                    settings.myPrimaryDemoColor = myPrimaryColorChooser.selectedColor!!
                }
            }
            row("Secondary Color:") {
                mySecondaryColorChooser = ColorPanel()
                cell(mySecondaryColorChooser)
                mySecondaryColorChooser.bindColor(
                    settings::mySecondaryColor,
                    settings::mySecondaryColor::set
                )
                mySecondaryColorChooser.addActionListener {
                    settings.mySecondaryDemoColor = mySecondaryColorChooser.selectedColor!!
                }
            }
            row("Indeterminate") {
                cell(indeterminateExampleProgressBar)
            }
            row("Determinate") {
                cell(determinateExampleProgressBar)
            }
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
        return myPrimaryColorChooser.selectedColor != settings.myPrimaryColor
                || mySecondaryColorChooser.selectedColor != settings.mySecondaryColor
                || panel.isModified()
    }

    override fun reset() {
        val settings = CustomProgressBarSettings.instance
        settings.myPrimaryDemoColor = settings.myPrimaryColor
        settings.mySecondaryDemoColor = settings.mySecondaryColor
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
        settings.myPrimaryColor = myPrimaryColorChooser.selectedColor!!
        settings.myPrimaryDemoColor = settings.myPrimaryColor
        settings.mySecondaryColor = mySecondaryColorChooser.selectedColor!!
        settings.mySecondaryDemoColor = settings.mySecondaryColor
        panel.apply()
    }

    override fun getDisplayName(): String {
        return "Custom Progress Bar"
    }

    override fun getId(): String {
        return "preferences.custom.progress.bar"
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
        job.cancel()
    }
}

fun ColorPanel.bindColor(getter: () -> Color?, setter: KFunction1<Color, Unit>) {
    this.selectedColor = getter()
    selectedColor?.let { setter(it) }
}