package org.phdeh.a.gui

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

data class Edge(
    val from: Node,
    var to: Node,
    val win: Window
) : ScreenObject {
    var shortest = false

    override fun isVisible(r: Screen): Boolean {
        val x = (from.x + to.x) / 2
        val y = (from.y + to.y) / 2
        val width = Math.abs(from.x - to.x)
        val height = Math.abs(from.y - to.y)
        return Math.abs(x - r.x) < (r.width + width) / 2 &&
                Math.abs(y - r.y) < (r.height + height) / 2
    }

    override fun draw(g: Graphics2D, r: Screen) {
        val dx = (-r.width / 2 + r.x).toInt()
        val dy = (-r.height / 2 + r.y).toInt()
        if (shortest)
            g.stroke = BasicStroke(6f)
        else
            g.stroke = BasicStroke(3f)
        g.color = Color.BLACK
        g.drawLine(
            from.x.toInt() - dx,
            from.y.toInt() - dy,
            to.x.toInt() - dx,
            to.y.toInt() - dy
        )

        if (shortest) {
            g.stroke = BasicStroke(3f)
            g.color = Color.GREEN
            g.drawLine(
                from.x.toInt() - dx,
                from.y.toInt() - dy,
                to.x.toInt() - dx,
                to.y.toInt() - dy
            )
        }
    }
}