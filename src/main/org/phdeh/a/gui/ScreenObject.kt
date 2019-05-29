package org.phdeh.a.gui

import java.awt.Graphics2D
import java.awt.Rectangle

interface ScreenObject {
    fun draw(g: Graphics2D, r: Screen)

    fun isVisible(r: Screen): Boolean
}