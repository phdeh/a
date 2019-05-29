package org.phdeh.a.gui

import java.awt.event.*

class Mouse(
    private val window: Window,
    private val action: (Mouse) -> Unit
) {
    private var _x = 0
    private var _y = 0
    private var _dx = 0
    private var _dy = 0
    private var _lb = false
    private var _rb = false
    private var _mb = false
    private var _lb_r = false
    private var _rb_r = false
    private var _mb_r = false
    private var _lb_p = false
    private var _rb_p = false
    private var _mb_p = false
    private var _hw = 0
    private var _vw = 0

    val x get() = _x
    val y get() = _y
    val dx get() = _dx
    val dy get() = _dy
    val leftButton get() = _lb
    val rightButton get() = _rb
    val middleButton get() = _mb
    val leftButtonReleased get() = _lb_r
    val rightButtonReleased get() = _rb_r
    val middleButtonReleased get() = _mb_r
    val leftButtonPressed get() = _lb_p
    val rightButtonPressed get() = _rb_p
    val middleButtonPressed get() = _mb_p
    val horisontalWheel get() = _hw
    val verticalWheel get() = _vw

    init {
        fun handle(e: MouseEvent, mb: Boolean? = null) {
            val px = _x
            val py = _y
            _x = e.x
            _y = e.y
            _dx = _x - px
            _dy = _y - py

            val plb = _lb
            val prb = _rb
            val pmb = _mb

            if (mb != null)
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        _lb = mb
                    }
                    MouseEvent.BUTTON3 -> {
                        _rb = mb
                    }
                    MouseEvent.BUTTON2 -> {
                        _mb = mb
                    }
                }

            _lb_p = !plb && _lb
            _rb_p = !prb && _rb
            _mb_p = !pmb && _mb

            _lb_r = plb && !_lb
            _rb_r = prb && !_rb
            _mb_r = pmb && !_mb

            _hw = 0
            _vw = 0
        }

        val ml = object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                handle(e, null)
                action(this@Mouse)
            }

            override fun mouseEntered(e: MouseEvent) {
                handle(e, null)
                action(this@Mouse)
            }

            override fun mouseExited(e: MouseEvent) {
                handle(e, null)
                action(this@Mouse)
            }

            override fun mousePressed(e: MouseEvent) {
                handle(e, true)
                action(this@Mouse)
            }

            override fun mouseReleased(e: MouseEvent) {
                handle(e, false)
                action(this@Mouse)
            }
        }
        val mml = object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                handle(e, true)
                action(this@Mouse)
            }

            override fun mouseMoved(e: MouseEvent) {
                handle(e, false)
                action(this@Mouse)
            }
        }
        val mwl = object : MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                handle(e, null)
                if (e.isShiftDown)
                    _hw = e.wheelRotation
                else
                    _vw = e.wheelRotation
                action(this@Mouse)
            }
        }
        window.addMouseListener(ml)
        window.addMouseMotionListener(mml)
        window.addMouseWheelListener(mwl)
    }
}