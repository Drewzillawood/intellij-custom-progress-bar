package com.drewzillawood.customprogressbar.component

import com.drewzillawood.customprogressbar.settings.CustomProgressBarSettings
import com.intellij.openapi.progress.util.ColorProgressBar
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.UIUtil
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
import javax.swing.JComponent
import javax.swing.JProgressBar
import javax.swing.SwingConstants
import javax.swing.plaf.basic.BasicProgressBarUI

open class CustomProgressBarUI : BasicProgressBarUI() {

    private val TRACK_COLOR: Color = JBColor.namedColor("ProgressBar.trackColor", JBColor(Gray.xC4, Gray.x55))
    private val PROGRESS_COLOR: Color = JBColor.namedColor("ProgressBar.progressColor", JBColor(Gray.x80, Gray.xA0))
    private val INDETERMINATE_START_COLOR: Color = JBColor.namedColor("ProgressBar.indeterminateStartColor", JBColor(Gray.xC4, Gray.x69))
    private val INDETERMINATE_END_COLOR: Color = JBColor.namedColor("ProgressBar.indeterminateEndColor", JBColor(Gray.x80, Gray.x83))

    private val FAILED_COLOR: Color = JBColor.namedColor("ProgressBar.failedColor", JBColor(0xd64f4f, 0xe74848))
    private val FAILED_END_COLOR: Color = JBColor.namedColor("ProgressBar.failedEndColor", JBColor(0xfb8f89, 0xf4a2a0))
    private val PASSED_COLOR: Color = JBColor.namedColor("ProgressBar.passedColor", JBColor(0x34b171, 0x008f50))
    private val PASSED_END_COLOR: Color = JBColor.namedColor("ProgressBar.passedEndColor", JBColor(0x7ee8a5, 0x5dc48f))
    private val WARNING_COLOR: Color = JBColor.namedColor("ProgressBar.warningColor", JBColor(0xF0A732, 0xD9A343))
    private val WARNING_END_COLOR: Color = JBColor.namedColor("ProgressBar.warningEndColor", JBColor(0xEAD2A1, 0xEAD2A1))

    private val CYCLE_TIME_DEFAULT = 800
    private val REPAINT_INTERVAL_DEFAULT = 50

    private val CYCLE_TIME_SIMPLIFIED = 1000
    private val REPAINT_INTERVAL_SIMPLIFIED = 500
    private val ourCycleTime = CYCLE_TIME_DEFAULT
    private val ourRepaintInterval = REPAINT_INTERVAL_DEFAULT

    private val DEFAULT_WIDTH = 4

    private val settings = CustomProgressBarSettings.instance

    override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
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

            // Use foreground color as a reference, don't use it directly. This is done for compatibility reason.
            // Colors are hardcoded in UI delegates by design. If more colors are needed contact designers.
            val startColor: Color = settings.myPrimaryColor
            val endColor: Color = settings.mySecondaryColor

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
                    (r.x + animationIndex * step * 2).toFloat(), yOffset.toFloat(), startColor,
                    (r.x + frameCount * step + animationIndex * step * 2).toFloat(), yOffset.toFloat(), endColor, true
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

    protected fun getStartColor(c: JComponent?): Color {
        return INDETERMINATE_START_COLOR
    }

    protected fun getEndColor(c: JComponent?): Color {
        return INDETERMINATE_END_COLOR
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

    protected fun getRemainderColor(): Color {
        return settings.mySecondaryColor
    }

    protected fun getFinishedColor(c: JComponent?): Color {
        return settings.myPrimaryColor
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
        val flatEnds = UIUtil.isUnderWin10LookAndFeel() || progressBar.getClientProperty("ProgressBar.flatEnds") == true
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
            g2.color = getRemainderColor()
            g2.fill(fullShape)

            // Use foreground color as a reference, don't use it directly. This is done for compatibility reason.
            // Colors are hardcoded in UI delegates by design. If more colors are needed contact designers.
            val foreground = progressBar.foreground
            if (foreground === ColorProgressBar.RED) {
                g2.color = FAILED_COLOR
            } else if (foreground === ColorProgressBar.GREEN) {
                g2.color = PASSED_COLOR
            } else if (foreground === ColorProgressBar.YELLOW) {
                g2.color = WARNING_COLOR
            } else {
                g2.color = getFinishedColor(c)
            }
            g2.fill(coloredShape)

            // Paint text
            if (progressBar.isStringPainted) {
                paintString(g, i.left, i.top, r.width, r.height, amountFull, i)
            }
        } finally {
            g2.dispose()
        }
    }
}