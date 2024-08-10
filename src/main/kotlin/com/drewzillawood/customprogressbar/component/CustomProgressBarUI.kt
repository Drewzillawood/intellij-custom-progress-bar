package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.data.PersistentConfigsComponent
import com.intellij.ide.ui.laf.darcula.ui.DarculaProgressBarUI
import com.intellij.openapi.components.service
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ImageLoader
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Dimension
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Insets
import java.awt.Paint
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
  private var indeterminateOffset = 0

  @Volatile
  private var velocity = 1

  private val DEFAULT_WIDTH = 4

  private lateinit var paintLoadedBackground: Paint

  private var current = service<PersistentConfigsComponent>().state

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

  override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
    val g2d = g as? Graphics2D ?: return
    c ?: return

    g2d.drawProgressBar(c) { insets, w, h, barRectWidth, barRectHeight ->
      g2d.drawBorder(component = c, width = w, height = h)
      g2d.drawProgression(g = g, c = c, width = w, height = h)

      val loadingImage: BufferedImage = toBufferedImage(ImageLoader.loadFromUrl(File(current.imagePath!!).toURI().toURL())
        ?.getScaledInstance(20, 20, Image.SCALE_SMOOTH)!!)

      indeterminateOffset += velocity
      if (indeterminateOffset <= loadingImage.width / 2) {
        indeterminateOffset = loadingImage.width / 2
        velocity = 1
      } else if (indeterminateOffset >= w + loadingImage.width / 2) {
        indeterminateOffset = w + loadingImage.width / 2
        velocity = -1
      }

      g2d.drawLoadingImage(
        component = c,
        image = loadingImage,
        offset = indeterminateOffset.toFloat()
      )
      g2d.drawUndeterminedText(
        component = c,
        insets = insets,
        height = h,
        barRectWidth = barRectWidth,
        barRectHeight = barRectHeight
      )
    }
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
    return size
  }

//  override fun getPreferredSize(c: JComponent?): Dimension {
//    val colors = arrayOf(getIndeterminatePrimaryColor(), getIndeterminateSecondaryColor())
//    paintLoadedBackground = LinearGradientPaint(
//      0f,
//      JBUIScale.scale(2f),
//      0f,
//      progressBar.height - JBUIScale.scale(6f),
//      colors.indices.map { it.toFloat() / (colors.size - 1) }.toFloatArray(),
//      colors
//    )
//    val imageHeight = 20
//    return Dimension(super.getPreferredSize(c).width, (imageHeight + MARGIN * 4).toInt())
//  }

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
    val g2 = g?.create() as Graphics2D
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

      val r = Rectangle(progressBar.size)
      if (c!!.isOpaque && c.parent != null) {
        g2.color = c.parent.background
        g2.fill(r)
      }

      val i = progressBar.insets
      JBInsets.removeFrom(r, i)
      val amountFull = getAmountFull(i, r.width, r.height)

      val fullShape: Shape
      val coloredShape: Shape
      val orientation = progressBar.orientation
      if (orientation == SwingConstants.HORIZONTAL) {
        val pHeight = progressBar.preferredSize.height
        val yOffset = r.y + (r.height - pHeight) / 2

        fullShape = getShapedRect(
          r.x.toFloat(),
          yOffset.toFloat(),
          r.width.toFloat(),
          pHeight.toFloat(),
          pHeight.toFloat()
        )
        coloredShape = getShapedRect(
          r.x.toFloat(),
          yOffset.toFloat(),
          amountFull.toFloat(),
          pHeight.toFloat(),
          pHeight.toFloat()
        )
      } else {
        val pWidth = progressBar.preferredSize.width
        val xOffset = r.x + (r.width - pWidth) / 2

        fullShape = getShapedRect(
          xOffset.toFloat(),
          r.y.toFloat(),
          pWidth.toFloat(),
          r.height.toFloat(),
          pWidth.toFloat()
        )
        coloredShape = getShapedRect(
          xOffset.toFloat(),
          r.y.toFloat(),
          pWidth.toFloat(),
          amountFull.toFloat(),
          pWidth.toFloat()
        )
      }
      g2.color = getDeterminateSecondaryColor()
      g2.fill(fullShape)

      // Use foreground color as a reference, don't use it directly. This is done for compatibility reason.
      // Colors are hardcoded in UI delegates by design. If more colors are needed contact designers.
      g2.color = getDeterminatePrimaryColor()
      g2.fill(coloredShape)

      // Paint text
      if (progressBar.isStringPainted) {
        paintString(g, i.left, i.top, r.width, r.height, amountFull, i)
      }
    } finally {
      g2.dispose()
    }
  }

  open fun getDeterminatePrimaryColor(): Color {
    return Color(current.myDeterminatePrimaryColor)
  }

  open fun getDeterminateSecondaryColor(): Color {
    return Color(current.myDeterminateSecondaryColor)
  }

  private fun toBufferedImage(img: Image): BufferedImage {
    if (img is BufferedImage) {
      return img
    }

    // Create a buffered image with transparency
    val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

    // Draw the image on to the buffered image
    val bGr = bimage.createGraphics()
    bGr.drawImage(img, 0, 0, null)
    bGr.dispose()

    return bimage
  }

  private fun isEven(value: Int): Boolean = value % 2 == 0

  private fun Graphics2D.drawBorder(component: JComponent, height: Int, width: Int) {
    val outsideRadius = JBUIScale.scale(9f)
    val insideRadius = JBUIScale.scale(8f)

    val parent = component.parent
    val background = if (parent != null) parent.background else UIUtil.getPanelBackground()

    translate(0, (component.height - height) / 2)
    color = progressBar.foreground
    fill(
      RoundRectangle2D.Float(
        0f,
        0f,
        width - SCALED_MARGIN,
        height - SCALED_MARGIN,
        outsideRadius,
        outsideRadius
      )
    )

    color = background
    fill(
      RoundRectangle2D.Float(
        SCALED_MARGIN,
        SCALED_MARGIN,
        width - 2f * SCALED_MARGIN - SCALED_MARGIN,
        height - 2f * SCALED_MARGIN - SCALED_MARGIN,
        insideRadius,
        insideRadius
      )
    )
  }

  private fun Graphics2D.drawLoadingImage(component: JComponent, image: BufferedImage?, offset: Float) {
    image ?: return

    val verticalMargin = (component.height - image.height) / 2f
    val horizontalMargin = 0f
    val maxedOffset = min(
      max(horizontalMargin, offset - image.width / 2f),
      component.width - image.width - horizontalMargin
    )

    drawImage(
      image,
      affineTransform(
        tx = maxedOffset,
        ty = verticalMargin,
        sx = 1f,
        sy = 1f
      ),
      null
    )
  }

  private fun Graphics2D.drawProgression(g: Graphics?, c: JComponent, width: Int, height: Int) {
    val g2 = g?.create() as Graphics2D
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

      val r = Rectangle(progressBar.size)
      if (c.isOpaque) {
        g2.color = c.parent.background
        g2.fill(r)
      }

      val i = progressBar.insets
      JBInsets.removeFrom(r, i)
      val orientation = progressBar.orientation

      val startColor: Color = getIndeterminatePrimaryColor()
      val endColor: Color = getIndeterminateSecondaryColor()

      val pHeight = progressBar.preferredSize.height
      val pWidth = progressBar.preferredSize.width

      var yOffset = r.y + (r.height - pHeight) / 2
      var xOffset = r.x + (r.width - pWidth) / 2

      val shape: Shape
      val step = JBUIScale.scale(6)
      if (orientation == SwingConstants.HORIZONTAL) {
        shape = getShapedRect(
          r.x.toFloat(),
          yOffset.toFloat(),
          r.width.toFloat(),
          pHeight.toFloat(),
          pHeight.toFloat()
        )
        yOffset = r.y + pHeight / 2
        g2.paint = GradientPaint(
          (r.x + animationIndex * step * 2).toFloat(),
          yOffset.toFloat(),
          startColor,
          (r.x + frameCount * step + animationIndex * step * 2).toFloat(),
          yOffset.toFloat(),
          endColor,
          true
        )
      } else {
        shape = getShapedRect(
          xOffset.toFloat(),
          r.y.toFloat(),
          pWidth.toFloat(),
          r.height.toFloat(),
          pWidth.toFloat()
        )
        xOffset = r.x + pWidth / 2
        g2.paint = GradientPaint(
          xOffset.toFloat(), (r.y + animationIndex * step * 2).toFloat(), startColor,
          xOffset.toFloat(), (r.y + frameCount * step + animationIndex * step * 2).toFloat(), endColor, true
        )
      }
      g2.fill(
        RoundRectangle2D.Float(
          10f * SCALED_MARGIN,
          10f * SCALED_MARGIN,
          JBUIScale.scale(width * 1f)/*width - JBUIScale.scale(20f)*/,
          /*height - */JBUIScale.scale(5f),
          SCALED_PROGRESSION_RADIUS,
          SCALED_PROGRESSION_RADIUS
        )
      )
      g2.fill(shape)

      // Paint text
//      if (progressBar.isStringPainted) {
//        if (progressBar.orientation == SwingConstants.HORIZONTAL) {
//          paintString(g as Graphics2D, i.left, i.top, r.width, r.height, boxRect.x, boxRect.width)
//        } else {
//          paintString(g as Graphics2D, i.left, i.top, r.width, r.height, boxRect.y, boxRect.height)
//        }
//      }
    } finally {
      g2.dispose()
    }
  }

//  private fun Graphics2D.drawProgression(width: Int, height: Int) {
//    paint = paintLoadedBackground
//    fill(
//      RoundRectangle2D.Float(
//        2f * SCALED_MARGIN,
//        2f * SCALED_MARGIN,
//        width - JBUIScale.scale(5f),
//        height - JBUIScale.scale(5f),
//        SCALED_PROGRESSION_RADIUS,
//        SCALED_PROGRESSION_RADIUS
//      )
//    )
//  }

  private fun Graphics2D.drawDeterminedText(
    component: JComponent,
    insets: Insets,
    offsetX: Int,
    height: Int,
    barRectWidth: Int,
    barRectHeight: Int
  ) {
    if (progressBar.isStringPainted) {
      translate(0, -(component.height - height) / 2)

      paintString(
        this, insets.left, insets.top,
        barRectWidth, barRectHeight,
        offsetX, insets
      )
    }
  }

  private fun Graphics2D.drawUndeterminedText(
    component: JComponent,
    insets: Insets,
    height: Int,
    barRectWidth: Int,
    barRectHeight: Int
  ) {
    if (progressBar.isStringPainted) {
      translate(0, -(component.height - height) / 2)
      if (progressBar.orientation == SwingConstants.HORIZONTAL) {
        paintString(insets.left, insets.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
      } else {
        paintString(insets.left, insets.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
      }
    }
  }

  private fun Graphics2D.paintString(x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
    val progressString = progressBar.string
    font = progressBar.font
    var renderLocation = getStringPlacement(
      this, progressString,
      x, y, w, h
    )
    val oldClip = clipBounds

    when (progressBar.orientation) {
      SwingConstants.HORIZONTAL -> {
        color = selectionBackground
        drawString(progressString, renderLocation.x, renderLocation.y)
        color = selectionForeground
        clipRect(fillStart, y, amountFull, h)
        drawString(progressString, renderLocation.x, renderLocation.y)
      }

      SwingConstants.VERTICAL -> {
        color = selectionBackground
        val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
        font = progressBar.font.deriveFont(rotate)
        renderLocation = getStringPlacement(
          this, progressString,
          x, y, w, h
        )
        drawString(progressString, renderLocation.x, renderLocation.y)
        color = selectionForeground
        clipRect(x, fillStart, w, amountFull)
        drawString(progressString, renderLocation.x, renderLocation.y)
      }
    }
    clip = oldClip
  }

  private inline fun Graphics2D.drawProgressBar(
    component: JComponent,
    block: (Insets, w: Int, wh: Int, barRectWidth: Int, barRectHeight: Int) -> Unit
  ) {
    if (progressBar.orientation != SwingConstants.HORIZONTAL || !component.componentOrientation.isLeftToRight) {
      super.paintDeterminate(this, component)
      return
    }

    val config = GraphicsUtil.setupAAPainting(this)

    val b = progressBar.insets
    val w = progressBar.width
    var h = progressBar.preferredSize.height
    if (!isEven(component.height - h)) h++

    val barRectWidth = w - (b.right + b.left)
    val barRectHeight = h - (b.top + b.bottom)

    if (barRectWidth <= 0 || barRectHeight <= 0) {
      return
    }

    block(b, w - 20, h, barRectWidth, barRectHeight)
    config.restore()
  }

  companion object {
    private const val MARGIN = 1f
    private val SCALED_MARGIN = JBUIScale.scale(MARGIN)
    private val SCALED_PROGRESSION_RADIUS = JBUIScale.scale(5f)
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