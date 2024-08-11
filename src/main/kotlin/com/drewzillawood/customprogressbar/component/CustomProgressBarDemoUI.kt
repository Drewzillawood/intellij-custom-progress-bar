package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.data.PersistentDemoConfigsComponent
import com.intellij.openapi.components.service
import com.intellij.util.ImageLoader
import java.awt.Color
import java.awt.Image
import java.io.File
import javax.swing.UIManager

open class CustomProgressBarDemoUI : CustomProgressBarUI() {

  private var currentDemo = service<PersistentDemoConfigsComponent>().state

  override fun updateIndeterminateAnimationIndex(startMillis: Long) {
    val numFrames = currentDemo.cycleTime / currentDemo.repaintInterval
    val timePassed = System.currentTimeMillis() - startMillis
    this.animationIndex = (timePassed / currentDemo.repaintInterval.toLong() % numFrames.toLong()).toInt()
  }

  override fun installDefaults() {
    super.installDefaults()
    UIManager.put("ProgressBar.repaintInterval", currentDemo.repaintInterval)
    UIManager.put("ProgressBar.cycleTime", currentDemo.cycleTime)
  }

  override fun isCustomImageEnabled(): Boolean {
    return currentDemo.isCustomImageEnabled
  }

  override fun loadImageAndScale() = ImageLoader.loadFromUrl(File(currentDemo.imagePath!!).toURI().toURL())
    ?.getScaledInstance(16, 16, Image.SCALE_SMOOTH)!!

  override fun getIndeterminateSecondaryColor(): Color {
    return Color(currentDemo.myIndeterminateSecondaryColor)
  }

  override fun getIndeterminatePrimaryColor(): Color {
    return Color(currentDemo.myIndeterminatePrimaryColor)
  }

  override fun getDeterminatePrimaryColor(): Color {
    return Color(currentDemo.myDeterminatePrimaryColor)
  }

  override fun getDeterminateSecondaryColor(): Color {
    return Color(currentDemo.myDeterminateSecondaryColor)
  }
}