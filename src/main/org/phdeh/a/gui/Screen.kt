package org.phdeh.a.gui

data class Screen(
    private val camera: Vector,
    private val window: Window
) {
    val x get() = camera.x
    val y get() = camera.y

    val width get() = window.width
    val height get() = window.height
}