package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.domain.GetConfigUseCase
import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import com.intellij.ide.ui.laf.darcula.ui.DarculaProgressBarUI
import com.intellij.openapi.components.service
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.UIUtilities
import java.awt.Color
import java.awt.Dimension
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JProgressBar
import javax.swing.SwingConstants
import javax.swing.UIManager

open class CustomProgressBarUI : DarculaProgressBarUI() {

    private val DEFAULT_WIDTH = 4

    private val settings = CustomProgressBarSettings.getInstance()
    private val getConfig = service<GetConfigUseCase>()
    private var current = getConfig()

    override fun updateIndeterminateAnimationIndex(startMillis: Long) {
        val numFrames = settings.cycleTime / settings.repaintInterval
        val timePassed = System.currentTimeMillis() - startMillis
        this.animationIndex = (timePassed / settings.repaintInterval.toLong() % numFrames.toLong()).toInt()
    }

    override fun installDefaults() {
        super.installDefaults()
        UIManager.put("ProgressBar.repaintInterval", settings.repaintInterval)
        UIManager.put("ProgressBar.cycleTime", settings.cycleTime)
    }

    override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
        if (!settings.isCustomProgressBarEnabled) {
            super.paintIndeterminate(g, c)
            return
        }

        val g2 = g?.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

            val r = Rectangle(progressBar.size)
            if (c!!.isOpaque) {
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
                val image = ImageIO.read(File("C:\\Users\\drew\\IdeaProjects\\CustomProgressBar\\src\\main\\resources\\META-INF\\pluginIcon.svg"))
//                g2.drawRenderedImage(image, xOffset, yOffset, null)
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
            g2.fill(shape)

            // Paint text
            if (progressBar.isStringPainted) {
                if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                    paintString(g as Graphics2D, i.left, i.top, r.width, r.height, boxRect.x, boxRect.width)
                } else {
                    paintString(g as Graphics2D, i.left, i.top, r.width, r.height, boxRect.y, boxRect.height)
                }
            }
        } finally {
            g2.dispose()
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
        return size
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
        if(!settings.isCustomProgressBarEnabled) {
            super.paintDeterminate(g, c)
            return
        }
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
}