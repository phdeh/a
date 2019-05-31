package org.phdeh.a.gui

import java.awt.Color

class ColorLobby {
    private val colors = listOf(
        Color(255, 220, 0),
        Color(100, 180, 100),
        Color(100, 120, 255),
        Color(220, 125, 200),
        Color(255, 120, 0),
        Color(0, 200, 200),
        Color(240, 255, 0),
        Color(255, 0, 0),
        Color(180, 0, 255),
        Color(0, 255, 255),
        Color(0, 0, 180),
        Color(0, 255, 180)
    )

    private var index = 0

    fun get(): Color {
        return colors[index++ % colors.size]
    }
}