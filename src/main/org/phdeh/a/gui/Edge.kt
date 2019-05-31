package org.phdeh.a.gui

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

data class Edge(
    val from: Node,
    var to: Node,
    val win: Window
) : ScreenObject {
    private val width = 3f
    private var actuallyShortest = false
    private val colors = mutableSetOf<Color>()
    var shortest
        get() = actuallyShortest
        set(value) {
            actuallyShortest = value
            if (!value)
                colors.clear()
        }

    fun addColor(color: Color) {
        shortest = true
        colors += color
    }

    override fun isVisible(r: Screen): Boolean {
        val x = (from.x + to.x) / 2
        val y = (from.y + to.y) / 2
        val width = Math.abs(from.x - to.x)
        val height = Math.abs(from.y - to.y)
        return Math.abs(x - r.x) < (r.width + width) / 2 &&
                Math.abs(y - r.y) < (r.height + height) / 2
    }

    override fun draw(g: Graphics2D, r: Screen) {
        val oddness = (from.x < to.x) xor (from.y < to.y)
        val from = if (oddness) this.from else this.to
        val to = if (oddness) this.to else this.from

        val dx = (-r.width / 2 + r.x).toInt()
        val dy = (-r.height / 2 + r.y).toInt()
        if (!shortest) {
            g.stroke = BasicStroke(3f)
            g.color = Color.BLACK
            g.drawLine(
                from.x.toInt() - dx,
                from.y.toInt() - dy,
                to.x.toInt() - dx,
                to.y.toInt() - dy
            )
        } else {
            val a = Math.atan2(to.y - from.y, to.x - from.x) + Math.PI / 2
//            g.stroke = BasicStroke(width * colors.size + width * 2)
//            g.color = Color.BLACK
//            g.drawLine(
//                from.x.toInt() - dx,
//                from.y.toInt() - dy,
//                to.x.toInt() - dx,
//                to.y.toInt() - dy
//            )
            g.stroke = BasicStroke(width)
            colors.forEachIndexed { i, it ->
                val shift = (colors.size - 1) * width / 2 - i * width
                g.color = it
                g.drawLine(
                    (from.x - dx + Math.cos(a) * shift).toInt(),
                    (from.y - dy + Math.sin(a) * shift).toInt(),
                    (to.x - dx + Math.cos(a) * shift).toInt(),
                    (to.y - dy + Math.sin(a) * shift).toInt()
                )
            }
        }
    }
}