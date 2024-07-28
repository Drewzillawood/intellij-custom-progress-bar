package com.drewzillawood.customprogressbar.settings

import com.drewzillawood.customprogressbar.component.CustomProgressBarDemoUI
import com.drewzillawood.customprogressbar.data.PersistentConfigsComponent
import com.drewzillawood.customprogressbar.data.PersistentDemoConfigsComponent
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.impl.welcomeScreen.WelcomeScreenUIManager
import com.intellij.ui.ColorPanel
import com.intellij.ui.LayeredIcon
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindValue
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty
import com.intellij.ui.layout.selected
import com.intellij.util.ui.GraphicsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JSlider
import kotlin.reflect.KMutableProperty0

class CustomProgressBarConfigurable : SearchableConfigurable, CoroutineScope {

  private val CYCLE_TIME_DEFAULT = 800
  private val REPAINT_INTERVAL_DEFAULT = 50

  private val getConfig = service<PersistentConfigsComponent>()
  private val getDemoConfig = service<PersistentDemoConfigsComponent>()
  private var initial = getConfig.state
  private var current = getDemoConfig.state

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
      group("Indeterminate") {
        panel {
          row {
            panel {
              row("Primary:") {
                myIndeterminatePrimaryColorChooser = colorPanel(current::myIndeterminatePrimaryColor).component
              }.resizableRow()
              row("Secondary:") {
                myIndeterminateSecondaryColorChooser = colorPanel(current::myIndeterminateSecondaryColor).component
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
                indeterminateExampleProgressBar.setUI(CustomProgressBarDemoUI())
              }
            }
            row("Repaint Interval (ms):") {
              repaintIntervalSlider = slider(0, 200, 25, 50)
                .bindValue(current::repaintInterval)
                .component
              repaintIntervalSlider.addChangeListener {
                current.repaintInterval = repaintIntervalSlider.value
                indeterminateExampleProgressBar.setUI(CustomProgressBarDemoUI())
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
                myDeterminatePrimaryColorChooser = colorPanel(current::myDeterminatePrimaryColor).component
              }
              row("Secondary:") {
                myDeterminateSecondaryColorChooser = colorPanel(current::myDeterminateSecondaryColor).component
              }
            }
            cell(determinateExampleProgressBar)
          }
        }
      }
      group("Image") {
        panel {
          row {
            val svgIcon = IconLoader.getIcon(current.imagePath, CustomProgressBarConfigurable::class.java)
            val scaledIcon = LayeredIcon.layeredIcon(arrayOf(svgIcon)).scale(48.0F / svgIcon.iconWidth)
            cell(IconPreviewPanel(JBLabel(scaledIcon)))
            panel {
              row {
                cell(TextFieldWithBrowseButton())
              }
            }
          }
        }
      }
    }

    simulateProgress()

    return panel
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
    current.copyFrom(initial)
    panel.reset()
    super.reset()
  }

  override fun cancel() {
    reset()
    super.cancel()
  }

  @Throws(ConfigurationException::class)
  override fun apply() {
    initial.copyFrom(current)
    getConfig.loadState(initial)
  }

  override fun getDisplayName(): String {
    return "Custom Progress Bar"
  }

  override fun getId(): String {
    return "preferences.custom.progress.bar"
  }
}

fun Row.colorPanel(property: KMutableProperty0<Int>): Cell<ColorPanel> {
  return cell(ColorPanel()).bindColor(property)
}

fun <T: ColorPanel> Cell<T>.bindColor(property: KMutableProperty0<Int>): Cell<T> {
  val colorChooser = this.component as ColorPanel
  colorChooser.addActionListener {
    property.set(colorChooser.selectedColor!!.rgb)
  }
  return bind(
    { colorPanel -> colorPanel.selectedColor!!.rgb },
    { colorPanel, propColor -> colorPanel.selectedColor = Color(propColor) },
    property.toMutableProperty()
  )
}

private class IconPreviewPanel(component: JComponent): JPanel(BorderLayout()) {
  val radius = 4
  val size = 60

  init {
    isOpaque = false
    background = WelcomeScreenUIManager.getMainAssociatedComponentBackground()
    preferredSize = Dimension(size, size)
    minimumSize = Dimension(size, size)
    add(component)
  }

  override fun paintComponent(g: Graphics) {
    g.color = background
    val config = GraphicsUtil.setupRoundedBorderAntialiasing(g)
    g.fillRoundRect(0, 0, width, height, 2 * radius, 2 * radius)
    config.restore()
  }
}