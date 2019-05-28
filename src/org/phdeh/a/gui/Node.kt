package org.phdeh.a.gui

import java.awt.Color
import java.awt.Graphics2D

data class Node(
    var name: String,
    var x: Double,
    var y: Double,
    val win: Window,
    var tx: Double = 0.0,
    var ty: Double = 0.0
) : ScreenObject {
    val width = 50
    val height = 50

    override fun isVisible(r: Screen): Boolean {
        return Math.abs(x - r.x) < (r.width + width) / 2 &&
                Math.abs(y - r.y) < (r.height + height) / 2
    }

    override fun draw(g: Graphics2D, r: Screen) {
        tx = Math.round(x / win.squareSize).toDouble() * win.squareSize
        ty = Math.round(y / win.squareSize).toDouble() * win.squareSize

        val alpha = Math.max(160 - (Math.sqrt(
            (tx - x) * (tx - x) + (ty - y) * (ty - y)
        ) / win.squareSize * 255).toInt(), 0)
        g.color = Color(0, 0, 0, alpha)
        g.fillOval(
            (tx - r.x).toInt() - (width - r.width) / 2,
            (ty - r.y).toInt() - (height - r.height) / 2,
            width,
            height
        )
        if (win.lastTouch === this)
            g.color = Color.RED
        else if (win.prevTouch === this)
            g.color = Color.RED
        else
            g.color = Color.BLUE
        g.fillOval(
            (x - r.x).toInt() - (width - r.width) / 2,
            (y - r.y).toInt() - (height - r.height) / 2,
            width,
            height
        )
        g.color = Color.WHITE
        g.drawString(
            name,
            (x - r.x).toInt() + (r.width) / 2
                    - g.getFontMetrics().stringWidth(name) / 2,
            (y - r.y).toInt() + (r.height + 28) / 2
        )
    }
}