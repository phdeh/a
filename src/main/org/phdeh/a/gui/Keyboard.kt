package org.phdeh.a.gui

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*

class Keyboard(
    private val window: Window,
    private val action: (Keyboard) -> Unit
) {
    private val keys = BitSet(256)
    private val pkeys = BitSet(256)
    private var _typed = '\u0000'
    val typed get() = _typed

    operator fun get(keyCode: Int) =
        if (keyCode in 0..255)
            keys[keyCode]
        else false

    fun pressed(keyCode: Int) =
        if (keyCode in 0..255)
            keys[keyCode] && !pkeys[keyCode]
        else false

    fun released(keyCode: Int) =
        if (keyCode in 0..255)
            !keys[keyCode] && pkeys[keyCode]
        else false

    val command
        get() = this[KeyEvent.VK_CONTROL] ||
                this[KeyEvent.VK_ALT] ||
                this[KeyEvent.VK_META]

    val handle
        get() = this[KeyEvent.VK_SPACE]

    val remove
        get() = this[KeyEvent.VK_BACK_SPACE] ||
                this[KeyEvent.VK_DELETE]

    init {
        fun handle() {
            for (i in 0..255)
                pkeys[i] = keys[i]
        }

        window.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                handle()
                keys[e.keyCode] = true
                action(this@Keyboard)
            }

            override fun keyReleased(e: KeyEvent) {
                handle()
                keys[e.keyCode] = false
                action(this@Keyboard)
            }

            override fun keyTyped(e: KeyEvent) {
                _typed = e.keyChar
                action(this@Keyboard)
            }
        })
    }
}