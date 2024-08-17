package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.data.PersistentConfigsComponent
import com.intellij.ide.ui.laf.darcula.ui.DarculaProgressBarUI
import com.intellij.openapi.components.service
import com.intellij.ui.icons.EMPTY_ICON
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ImageLoader
import com.intellij.util.ui.ImageUtil
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.UIUtilities
import java.awt.Color
import java.awt.Dimension
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JComponent
import javax.swing.JProgressBar
import javax.swing.SwingConstants
import javax.swing.UIManager
import kotlin.math.max
import kotlin.math.min

open class CustomProgressBarUI : DarculaProgressBarUI() {

  @Volatile
  private var indeterminateOffset = -20

  private var velocity: Int

  private val DEFAULT_WIDTH = 4

  private var current = service<PersistentConfigsComponent>().state

  init {
    velocity = current.cycleTime / current.repaintInterval
  }

  override fun updateIndeterminateAnimationIndex(startMillis: Long) {
    val numFrames = current.cycleTime / current.repaintInterval
    val timePassed = System.currentTimeMillis() - startMillis
    this.animationIndex = (timePassed / current.repaintInterval.toLong() % numFrames.toLong()).toInt()
  }

  override fun installDefaults() {
    super.installDefaults()
    UIManager.put("ProgressBar.repaintInterval", current.repaintInterval)
    UIManager.put("ProgressBar.cycleTime", current.cycleTime)
  }

  override fun paintIndeterminate(g: Graphics?, component: JComponent?) {
    drawProgressBar(g, component) { c, g2d ->
      try {
        drawProgression(g2d, c)

        if (isCustomImageEnabled()) {
          drawCustomImage(g2d, c, true)
        }

        drawIndeterminateString(g2d)
      } finally {
        g2d.dispose()
      }
    }
  }

  private fun drawProgressBar(
    graphics: Graphics?,
    component: JComponent?,
    draw: (c: JComponent, g2d: Graphics2D) -> Unit
  ) {
    val g2d = graphics as? Graphics2D ?: return
    component ?: return

    if (progressBar.orientation != SwingConstants.HORIZONTAL || !component.componentOrientation.isLeftToRight) {
      super.paintDeterminate(graphics, component)
      return
    }

    if (isProgressBarInvalid()) return

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

    draw(component, g2d)
  }

  private fun isProgressBarInvalid(): Boolean {
    return progressBar.width - (progressBar.insets.left + progressBar.insets.right) <= 0
      || progressBar.height - (progressBar.insets.top + progressBar.insets.bottom) <= 0
  }

  private fun drawProgression(
    g2d: Graphics2D,
    c: JComponent,
  ) {
    val r = Rectangle(progressBar.size)
    if (c.isOpaque) {
      g2d.color = c.parent.background
      g2d.fill(r)
    }

    val pw = progressBar.preferredSize.width
    val ph = progressBar.preferredSize.height

    JBInsets.removeFrom(r, progressBar.insets)
    val (x, y, w, h, ar) = r.let {
      if (progressBar.orientation == SwingConstants.HORIZONTAL) {
        listOf(
          it.x.toFloat(),
          (it.y + (it.height - ph) / 2).toFloat() - SCALED_PROGRESSION_HEIGHT * 2,
          it.width.toFloat(),
          SCALED_PROGRESSION_RADIUS,
          SCALED_PROGRESSION_RADIUS
        )
      } else {
        listOf(
          (it.x + (it.width - pw) / 2).toFloat(),
          it.y.toFloat(),
          pw.toFloat(),
          it.height.toFloat(),
          it.height.toFloat()
        )
      }
    }

    val step = JBUIScale.scale(6)
    val (x1, y1, x2, y2) = r.let {
      if (progressBar.orientation == SwingConstants.HORIZONTAL) {
        val y1 = (it.y + ph / 2).toFloat()
        listOf(
          (it.x + animationIndex * step * 2).toFloat(),
          y1,
          (it.x + frameCount * step + animationIndex * step * 2).toFloat(),
          y1
        )
      } else {
        val x1 = (it.x + pw / 2).toFloat()
        listOf(
          x1,
          (it.y + animationIndex * step * 2).toFloat(),
          x1,
          (it.y + frameCount * step + animationIndex * step * 2).toFloat()
        )
      }
    }

    val shape = getShapedRect(x, y, w, h, ar)
    g2d.paint = GradientPaint(x1, y1, getIndeterminatePrimaryColor(), x2, y2, getIndeterminateSecondaryColor(), true)
    g2d.fill(shape)
    g2d.fill(
      RoundRectangle2D.Float(
        0f * SCALED_MARGIN,
        SCALED_PROGRESSION_HEIGHT * 2 * SCALED_MARGIN - 2,
        JBUIScale.scale(r.width * 1f),
        SCALED_PROGRESSION_HEIGHT,
        SCALED_PROGRESSION_RADIUS,
        SCALED_PROGRESSION_RADIUS
      )
    )
  }

  private fun drawCustomImage(g2d: Graphics2D, c: JComponent, isIndeterminate: Boolean) {
    val loadingImage: BufferedImage = toBufferedImage(loadImageAndScale())

    val offset = if (isIndeterminate) {
      incrementIndeterminateVelocity(loadingImage)
    } else {
      getAmountFull(progressBar.insets, progressBar.size.width, progressBar.size.height).toFloat()
    }

    val verticalMargin = (c.height - loadingImage.height) / 2f
    val horizontalMargin = JBUIScale.scale(-20f)
    val maxedOffset = min(
      max(horizontalMargin, offset - loadingImage.width / 2f),
      c.width - loadingImage.width - horizontalMargin
    )

    g2d.drawImage(
      loadingImage,
      affineTransform(
        tx = maxedOffset,
        ty = verticalMargin,
        sx = 1f,
        sy = 1f
      ),
      null
    )
  }

  private fun incrementIndeterminateVelocity(loadingImage: BufferedImage): Float {
    indeterminateOffset += velocity
    if (indeterminateOffset >= progressBar.size.width + loadingImage.width) {
      indeterminateOffset = -loadingImage.width
      velocity = current.cycleTime / current.repaintInterval
    }
    return indeterminateOffset.toFloat()
  }

  private fun drawIndeterminateString(g2d: Graphics2D) {
    val r = Rectangle(progressBar.size)
    if (progressBar.isStringPainted) {
      if (progressBar.orientation == SwingConstants.HORIZONTAL) {
        paintString(g2d, progressBar.insets.left, progressBar.insets.top, r.width, r.height, boxRect.x, boxRect.width)
      } else {
        paintString(g2d, progressBar.insets.left, progressBar.insets.top, r.width, r.height, boxRect.y, boxRect.height)
      }
    }
  }

  private fun paintString(g: Graphics2D, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
    val progressString = progressBar.string
    g.font = progressBar.font
    var renderLocation = getStringPlacement(g, progressString, x, y, w, h)
    val oldClip = g.clipBounds

    if (progressBar.orientation == SwingConstants.HORIZONTAL) {
      g.color = selectionBackground
      UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)

      g.color = selectionForeground
      g.clipRect(fillStart, y, amountFull, h)
      UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)
    } else { // VERTICAL
      g.color = selectionBackground
      val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
      g.font = progressBar.font.deriveFont(rotate)
      renderLocation = getStringPlacement(g, progressString, x, y, w, h)
      UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)

      g.color = selectionForeground
      g.clipRect(x, fillStart, w, amountFull)
      UIUtilities.drawString(progressBar, g, progressString, renderLocation.x, renderLocation.y)
    }
    g.clip = oldClip
  }

  open fun getIndeterminatePrimaryColor(): Color {
    return Color(current.myIndeterminatePrimaryColor)
  }

  open fun getIndeterminateSecondaryColor(): Color {
    return Color(current.myIndeterminateSecondaryColor)
  }

  override fun getPreferredSize(c: JComponent?): Dimension {
    val size = super.getPreferredSize(c)
    if (c !is JProgressBar) {
      return size
    }
    if (!c.isStringPainted) {
      if (c.orientation == SwingConstants.HORIZONTAL) {
        size.height = getStripeWidth()
      } else {
        size.width = getStripeWidth()
      }
    }
    return Dimension(size.width, (16 + MARGIN * 4).toInt())
  }

  private fun getStripeWidth(): Int {
    val ho = progressBar.getClientProperty("ProgressBar.stripeWidth")
    return if (ho != null) {
      try {
        JBUIScale.scale(ho.toString().toInt())
      } catch (nfe: NumberFormatException) {
        JBUIScale.scale(DEFAULT_WIDTH)
      }
    } else {
      JBUIScale.scale(DEFAULT_WIDTH)
    }
  }

  private fun getShapedRect(x: Float, y: Float, w: Float, h: Float, ar: Float): Shape {
    val flatEnds = progressBar.getClientProperty("ProgressBar.flatEnds") == true
    return if (flatEnds) Rectangle2D.Float(x, y, w, h) else RoundRectangle2D.Float(x, y, w, h, ar, ar)
  }

  override fun paintDeterminate(g: Graphics?, c: JComponent?) {
    val g2d = g?.create() as Graphics2D
    c ?: return

    try {
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
      val r = Rectangle(progressBar.size)

      if (c.isOpaque && c.parent != null) {
        g2d.color = c.parent.background
        g2d.fill(r)
      }

      val insets = progressBar.insets
      JBInsets.removeFrom(r, insets)
      val amountFull = getAmountFull(insets, r.width, r.height)

      val fullShape: Shape
      val coloredShape: Shape
      val orientation = progressBar.orientation
      if (orientation == SwingConstants.HORIZONTAL) {
        val yOffset = r.y + (r.height - progressBar.preferredSize.height) / 2 - 2
        fullShape = getShapedRect(r.x.toFloat(), yOffset.toFloat() + SCALED_PROGRESSION_HEIGHT * 2, r.width.toFloat(), SCALED_PROGRESSION_RADIUS, SCALED_PROGRESSION_RADIUS)
        coloredShape = getShapedRect(r.x.toFloat(), yOffset.toFloat() + SCALED_PROGRESSION_HEIGHT * 2, amountFull.toFloat(), SCALED_PROGRESSION_RADIUS, SCALED_PROGRESSION_RADIUS)
      } else {
        val xOffset = r.x + (r.width - progressBar.preferredSize.width) / 2 - 2
        fullShape = getShapedRect(xOffset.toFloat(), r.y.toFloat(), progressBar.preferredSize.width.toFloat(), r.height.toFloat(), progressBar.preferredSize.width.toFloat())
        coloredShape = getShapedRect(xOffset.toFloat(), r.y.toFloat(), progressBar.preferredSize.width.toFloat(), amountFull.toFloat(), progressBar.preferredSize.width.toFloat())
      }
      g2d.color = getDeterminateSecondaryColor()
      g2d.fill(fullShape)
      g2d.color = getDeterminatePrimaryColor()
      g2d.fill(coloredShape)

      if (isCustomImageEnabled()) {
        drawCustomImage(g2d, c, false)
      }

      // Paint text
      if (progressBar.isStringPainted) {
        paintString(g, insets.left, insets.top, r.width, r.height, amountFull, insets)
      }
    } finally {
      g2d.dispose()
    }
  }

  open fun isCustomImageEnabled(): Boolean {
    return current.isCustomImageEnabled
  }

  open fun loadImageAndScale(): Image = (
    current.imagePath
      ?.let {
        ImageLoader.loadFromUrl(File(it).toURI().toURL())
      } ?: EMPTY_ICON.image
    )
    ?.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
    ?: EMPTY_ICON.image

  open fun getDeterminatePrimaryColor(): Color {
    return Color(current.myDeterminatePrimaryColor)
  }

  open fun getDeterminateSecondaryColor(): Color {
    return Color(current.myDeterminateSecondaryColor)
  }

  private fun toBufferedImage(img: Image): BufferedImage {
    if (img is BufferedImage) return img

    val bufferedImage = ImageUtil.createImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val bufferedImageGraphics = bufferedImage.createGraphics()
    bufferedImageGraphics.drawImage(img, 0, 0, null)
    bufferedImageGraphics.dispose()

    return bufferedImage
  }

  companion object {
    private const val MARGIN = 1f
    private const val HEIGHT = 5f
    private val SCALED_MARGIN = JBUIScale.scale(MARGIN)
    private val SCALED_PROGRESSION_HEIGHT = JBUIScale.scale(HEIGHT)
    private val SCALED_PROGRESSION_RADIUS = JBUIScale.scale(HEIGHT)
  }

  private fun affineTransform(tx: Float, ty: Float, sx: Float, sy: Float): AffineTransform =
    AffineTransform().apply {
      scale(
        sx.toDouble(),
        sy.toDouble()
      )
      translate(
        tx.toDouble(),
        ty.toDouble()
      )
    }
}