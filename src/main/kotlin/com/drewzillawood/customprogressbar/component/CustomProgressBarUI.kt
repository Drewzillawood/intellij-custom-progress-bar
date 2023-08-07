package com.drewzillawood.customprogressbar.component

import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI

class CustomProgressBarUI : BasicProgressBarUI() {

    override fun paintDeterminate(g: Graphics?, c: JComponent?) {
        super.paintDeterminate(g, c)
    }

    override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
        super.paintIndeterminate(g, c)
    }
}