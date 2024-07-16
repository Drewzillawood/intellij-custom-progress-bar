package com.drewzillawood.customprogressbar.settings

import com.drewzillawood.customprogressbar.component.CustomProgressBarUI
import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.drewzillawood.customprogressbar.domain.GetConfigUseCase
import com.drewzillawood.customprogressbar.domain.SaveConfigUseCase
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindValue
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty
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
import kotlin.reflect.KMutableProperty0

class CustomProgressBarConfigurable : SearchableConfigurable, CoroutineScope {

  private val CYCLE_TIME_DEFAULT = 800
  private val REPAINT_INTERVAL_DEFAULT = 50


  private val getConfig = service<GetConfigUseCase>()
  private val saveConfig = service<SaveConfigUseCase>()
  private var initial = PersistentConfigs(getConfig())
  private var current = getConfig()

  private val indeterminateExampleProgressBar = JProgressBar()
  private val determinateExampleProgressBar = JProgressBar()

  override val coroutineContext = CoroutineScope(Job()).coroutineContext

  private lateinit var panel: DialogPanel
  private lateinit var myIndeterminatePrimaryColorChooser: ColorPanel
  private lateinit var myIndeterminateSecondaryColorChooser: ColorPanel
  private lateinit var myDeterminatePrimaryColorChooser: ColorPanel
  private lateinit var myDeterminateSecondaryColorChooser: ColorPanel
  private lateinit var advancedOptionsCheckBox: JCheckBox
  private lateinit var cycleTimeSlider: JSlider
  private lateinit var repaintIntervalSlider: JSlider

  init {
    indeterminateExampleProgressBar.setUI(CustomProgressBarUI())
    indeterminateExampleProgressBar.isIndeterminate = true

    determinateExampleProgressBar.setUI(CustomProgressBarUI())
    determinateExampleProgressBar.isIndeterminate = false
    determinateExampleProgressBar.minimum = 0
    determinateExampleProgressBar.maximum = 100
    determinateExampleProgressBar.value = 0
  }

  override fun createComponent(): JComponent {
    panel = panel {
      group("Indeterminate") {
        panel {
          row {
            panel {
              row("Primary:") {
                myIndeterminatePrimaryColorChooser = ColorPanel()
                cell(myIndeterminatePrimaryColorChooser).bindColor(current::myIndeterminatePrimaryColor)
                myIndeterminatePrimaryColorChooser.addActionListener {
                  current.myIndeterminatePrimaryColor = myIndeterminatePrimaryColorChooser.selectedColor!!.rgb
                }
              }.resizableRow()
              row("Secondary:") {
                myIndeterminateSecondaryColorChooser = ColorPanel()
                cell(myIndeterminateSecondaryColorChooser).bindColor(current::myIndeterminateSecondaryColor)
                myIndeterminateSecondaryColorChooser.addActionListener {
                  current.myIndeterminateSecondaryColor = myIndeterminateSecondaryColorChooser.selectedColor!!.rgb
                }
              }.resizableRow()
            }
            cell(indeterminateExampleProgressBar)
          }
          row {
            advancedOptionsCheckBox = checkBox("Advanced")
              .bindSelected(current::isAdvancedOptionsEnabled)
              .component
            advancedOptionsCheckBox.addChangeListener {
              current.isAdvancedOptionsEnabled = advancedOptionsCheckBox.isSelected
              if (advancedOptionsCheckBox.isSelected.not()) {
                cycleTimeSlider.value = CYCLE_TIME_DEFAULT
                repaintIntervalSlider.value = REPAINT_INTERVAL_DEFAULT
              }
            }
          }
          indent {
            row("Cycle Time (ms):") {
              cycleTimeSlider = slider(0, 2000, 250, 500)
                .bindValue(current::cycleTime)
                .component
              cycleTimeSlider.addChangeListener {
                current.cycleTime = cycleTimeSlider.value
                indeterminateExampleProgressBar.setUI(CustomProgressBarUI())
              }
            }
            row("Repaint Interval (ms):") {
              repaintIntervalSlider = slider(0, 200, 25, 50)
                .bindValue(current::repaintInterval)
                .component
              repaintIntervalSlider.addChangeListener {
                current.repaintInterval = repaintIntervalSlider.value
                indeterminateExampleProgressBar.setUI(CustomProgressBarUI())
              }
            }
          }.visibleIf(advancedOptionsCheckBox.selected)
        }
      }
      group("Determinate") {
        panel {
          row {
            panel {
              row("Primary:") {
                myDeterminatePrimaryColorChooser = ColorPanel()
                cell(myDeterminatePrimaryColorChooser).bindColor(current::myDeterminatePrimaryColor)
                myDeterminatePrimaryColorChooser.addActionListener {
                  current.myDeterminatePrimaryColor = myDeterminatePrimaryColorChooser.selectedColor!!.rgb
                }
              }
              row("Secondary:") {
                myDeterminateSecondaryColorChooser = ColorPanel()
                cell(myDeterminateSecondaryColorChooser).bindColor(current::myDeterminateSecondaryColor)
                myDeterminateSecondaryColorChooser.addActionListener {
                  current.myDeterminateSecondaryColor = myDeterminateSecondaryColorChooser.selectedColor!!.rgb
                }
              }
            }
            cell(determinateExampleProgressBar)
          }
        }
      }
      row {
        textFieldWithBrowseButton(null, null, FileChooserDescriptorFactory.createSingleFileDescriptor(), null)
      }
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
          determinateExampleProgressBar.value =
            (determinateExampleProgressBar.value + increment) % determinateExampleProgressBar.maximum
        }
    }
  }

  override fun isModified(): Boolean {
    return current != initial
  }

  override fun reset() {
    saveConfig(initial)
    current = getConfig()
    panel.reset()
    super.reset()
  }

  override fun cancel() {
    reset()
    super.cancel()
  }

  @Throws(ConfigurationException::class)
  override fun apply() {
    saveConfig(current)
    initial = PersistentConfigs(getConfig())
    current = getConfig()
    panel.apply()
  }

  override fun getDisplayName(): String {
    return "Custom Progress Bar"
  }

  override fun getId(): String {
    return "preferences.custom.progress.bar"
  }
}

fun <T : ColorPanel> Cell<T>.bindColor(prop: KMutableProperty0<Int>): Cell<T> {
  return bind(
    { colorPanel -> colorPanel.selectedColor!!.rgb },
    { colorPanel, propColor -> colorPanel.selectedColor = Color(propColor) },
    prop.toMutableProperty()
  )
}