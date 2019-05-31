package org.phdeh.a.gui

import org.phdeh.a.astar.AStar
import org.phdeh.a.astar.Graph
import org.phdeh.a.astar.forEachPair
import java.awt.*
import java.awt.Color.*
import javax.swing.JFrame
import java.awt.image.BufferedImage
import java.awt.Rectangle
import java.awt.event.ComponentEvent
import java.awt.event.ComponentAdapter
import java.awt.RenderingHints
import java.awt.event.KeyEvent

fun main() {
    Window.isVisible = true
}

object Window : JFrame("AStar Test") {

    @Volatile
    var renderBuffer = BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)

    val nodeFont = Font("Helvetica", Font.BOLD, 40)

    val cursorOnScreen = Vector()

    val camera = Vector()
    val screen = Screen(camera, this)

    val edges = mutableListOf<Edge>()
    val nodes = mutableListOf<Node>()

    val nameLobby = NameLobby()

    var imaginaryNode = null as Node?
    var currentNode = null as Node?
    var lastTouch = null as Node?
    var prevTouch = null as Node?
    var solution = false

    val lineColor = Color(160, 128, 255, 100)
    val squareSize = 75
    val deleteCorner = 100
    val lineWidth = 6
    val selectDistance = 25
    val wheelMultiplier = 5

    var state = WindowState.NONE

    init {
        setSize(500, 500)
        centreWindow()
        initResizeListener()
        renderBuffer.graphics.fontMetrics.getLineMetrics("", renderBuffer.graphics)
    }

    val keyboardHandler = Keyboard(this) {
        var shouldRepaint = false
        if (it.remove)
            if (lastTouch !== null || prevTouch !== null) {
                clearSolution()
                lastTouch = null
                prevTouch = null
                shouldRepaint = true
            }
        if (it.released(KeyEvent.VK_SPACE)) {
            synchronized(this@Window) {
                val start = lastTouch
                val stop = prevTouch

                if (start == null || stop == null)
                    return@synchronized

                val graph = Graph.build { graph ->
                    val vertices = mutableMapOf<Node, Graph.GraphBuilder.VertexBuilder>()
                    nodes.forEach {
                        vertices[it] = graph(it.name, it.x, it.y).vertexBuilder
                    }
                    edges.forEach {
                        val from = vertices[it.from]
                        val to = vertices[it.to]
                        if (from != null && to != null)
                            from - to
                    }
                }

                val paths = AStar.findAllPaths(graph[start.name], graph[stop.name])
                paths.forEach {
                    it.forEachPair { a, b ->
                        edges.forEach {
                            if (it.from.name == a.name && it.to.name == b.name ||
                                it.from.name == b.name && it.to.name == a.name)
                                it.shortest = true
                        }
                    }
                }
                solution = true
                shouldRepaint = true
            }
        }
        if (shouldRepaint)
            repaint()
    }

    val mouseHandler = Mouse(this) {
        var shouldRepaint = false

        if (it.horisontalWheel != 0 || it.verticalWheel != 0)
            shouldRepaint = true
        camera.x += it.horisontalWheel * wheelMultiplier
        camera.y += it.verticalWheel * wheelMultiplier

        if (it.leftButtonPressed) {
            clearSolution()
            val node = findCursorNode(it.x, it.y)
            if (keyboardHandler.command) {
                if (node != null) {
                    prevTouch = lastTouch
                    lastTouch = node
                    shouldRepaint = true
                }
            } else {
                if (node == null) {
                    state = WindowState.SET_VERTEX
                    imaginaryNode = Node(
                        nameLobby.getName(),
                        it.x + camera.x, it.y + camera.y, this
                    )
                } else {
                    state = WindowState.SET_VERTEX
                       nodes.removeAt(nodes.indexOf(node))
                    imaginaryNode = node
                }
                shouldRepaint = true
            }
        }

        if (it.rightButtonPressed) {
            val node = findCursorNode(it.x, it.y)
            if (node != null)
                clearSolution()
            else
                removeEdgesAt(it.x, it.y)
            currentNode = node
            shouldRepaint = true
        }

        val cn = currentNode
        if (cn != null) {
            if (it.rightButtonReleased) {
                val node = findCursorNode(it.x, it.y)
                if (node != null) {
                    edges += Edge(node, cn, this)
                }
                currentNode = null
            }
            shouldRepaint = true
        }


        val imn = imaginaryNode
        if (imn != null) {
            imn.x = it.x + camera.x - width / 2
            imn.y = it.y + camera.y - height / 2
            shouldRepaint = true
        }

        if (it.leftButtonReleased && imn != null) {
            val close = findGlobalNode(imn.tx.toInt(), imn.ty.toInt())
            if (close == null &&
                it.x >= -deleteCorner &&
                it.y >= -deleteCorner &&
                it.x <= width + deleteCorner &&
                it.y <= height + deleteCorner
            ) {
                imn.x = imn.tx
                imn.y = imn.ty
                nodes += imn
            } else {
                edges.removeAll { it.to === imn || it.from === imn }
                nameLobby.returnName(imn.name)
            }
            imaginaryNode = null
            shouldRepaint = true
        }

        if (shouldRepaint)
            repaint()
    }

    fun draw(g: Graphics2D) {
        listOf(edges, nodes).forEach {
            it.forEach {
                if (it.isVisible(screen))
                    it.draw(g, screen)
            }
        }
        val imn = imaginaryNode
        if (imn != null)
            imn.draw(g, screen)
        drawCurrentEdge(g)
    }

    fun drawCurrentEdge(g: Graphics2D) {
        val r = screen
        val from = currentNode
        if (from === null)
            return
        val to = mouseHandler
        g.stroke = BasicStroke(6f)
        g.color = Color.BLACK
        val dx = (-r.width / 2 + r.x).toInt()
        val dy = (-r.height / 2 + r.y).toInt()
        val a = Math.atan2(to.y + dy - from.y, to.x + dx - from.x)
        g.drawLine(
            from.x.toInt() - dx + (Math.cos(a) * from.width / 2).toInt(),
            from.y.toInt() - dy + (Math.sin(a) * from.height / 2).toInt(),
            to.x.toInt(),
            to.y.toInt()
        )
    }

    fun clearSolution() {
        if (solution) {
            edges.forEach { it.shortest = false }
            solution = false
        }
    }

    fun findCursorNode(x: Int, y: Int): Node? {
        return findGlobalNode(
            (x - width / 2 + camera.x).toInt(),
            (y - height / 2 + camera.y).toInt()
        )
    }

    fun findGlobalNode(x: Int, y: Int): Node? {
        var close = null as Node?
        nodes.forEach { i ->
            if ((x - i.x) * (x - i.x) +
                (y - i.y) * (y - i.y) <=
                selectDistance * selectDistance
            ) {
                close = i
            }
        }
        return close
    }

    fun removeEdgesAt(x: Int, y: Int) {
//        val r = screen
//
//        val dx = (-r.width / 2 + r.x).toInt()
//        val dy = (-r.height / 2 + r.y).toInt()
//
//        val delete = mutableListOf<Edge>()
//
//        edges.forEach {
//            val x1 = it.from.x.toInt() - dx
//            val y1 = it.from.y.toInt() - dy
//
//            val x2 = it.to.x.toInt() - dx
//            val y2 = it.to.y.toInt() - dy
//
//            val x3 = x2 - x1
//            val y3 = y2 - y1
//
//            val a = Math.atan2(it.to.y - it.from.y, it.to.x - it.from.x)
//
//            val mx = x - x1
//            val my = y - y2
//
//            val mx1 = mx * Math.cos(a) + my * Math.sin(a)
//            val my1 = mx * Math.sin(a) + my * Math.cos(a)
//
//            cursorOnScreen.x = mx.toDouble()
//            cursorOnScreen.y = my.toDouble()
//            println("$mx1:$my1")
//            println("$mx:$my")
//
//            val x5 = Math.sqrt(x3 * x3 + y3 * y3 + 0.0) / 2
//
//            if (Math.abs(my1) < 10 && Math.abs(mx1 - x5) < x5)
//                delete += it
//        }
//
//        edges.removeAll { it in delete }
    }

    ///////

    override fun paint(g: Graphics) {
        val r = renderBuffer
        val g2 = r.graphics as Graphics2D
        g2.color = WHITE
        g2.fillRect(0, 0, width, height)
        var rh = RenderingHints(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )
        g2.setRenderingHints(rh)
        rh = RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        g2.color = lineColor
        val linesHorizontally = (width / squareSize) / 2 + 2
        val linesVertically = (height / squareSize) / 2 + 2
        for (x in -linesHorizontally..linesHorizontally)
            g2.fillRect(
                (-camera.x % squareSize).toInt() - lineWidth / 2
                        + x * squareSize - squareSize + width / 2, 0, lineWidth, height
            )
        for (y in -linesVertically..linesVertically)
            g2.fillRect(
                0, (-camera.y % squareSize).toInt() - lineWidth / 2
                        + y * squareSize - squareSize + height / 2, width, lineWidth
            )
        g2.font = nodeFont
        g2.setRenderingHints(rh)
        draw(g2)
//        g2.color = BLACK
//        g2.fillRect(cursorOnScreen.x.toInt() - 25, cursorOnScreen.y.toInt() - 25, 50, 50)
        g.drawImage(r, 0, 0, this)
    }

    override fun setSize(width: Int, height: Int) {
        handleResize(width, height)
        super.setSize(width, height)
    }

    override fun setSize(d: Dimension) {
        handleResize(d.width, d.height)
        super.setSize(d)
    }

    override fun setBounds(r: Rectangle) {
        handleResize(r.width, r.height)
        super.setBounds(r)
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        handleResize(width, height)
        super.setBounds(x, y, width, height)
    }

    fun initResizeListener() {
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(evt: ComponentEvent?) {
                handleResize(width, height)
            }
        })
    }

    fun handleResize(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            renderBuffer = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
        }
    }

    fun centreWindow() {
        val dimension = Toolkit.getDefaultToolkit().getScreenSize()
        val x = ((dimension.getWidth() - this.width) / 2).toInt()
        val y = ((dimension.getHeight() - this.height) / 2).toInt()
        this.setLocation(x, y)
    }
}