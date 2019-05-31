package org.phdeh.a.gui

import org.junit.Test
import org.phdeh.a.astar.shouldBe
import org.phdeh.a.astar.shouldNotBe
import java.awt.GraphicsEnvironment
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent

internal class WindowTest {
    @Test
    fun testGui() {
        if (GraphicsEnvironment.isHeadless())
            return

        val wheeling = Window.squareSize * 2 / Window.wheelMultiplier
        val robotTimer = 100L

        Window.isVisible = true
        Window.toFront()
        val robot = Robot()
        Thread.sleep(robotTimer)
        robot.mouseMove(0, 0)
        Thread.sleep(robotTimer)
        robot.mouseMove(Window.x + Window.width / 2, Window.y + Window.height / 2)
        Thread.sleep(robotTimer)
        // Set first
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        // Move to second
        robot.mouseWheel(wheeling)
        Thread.sleep(robotTimer)
        robot.keyPress(KeyEvent.VK_SHIFT)
        Thread.sleep(robotTimer)
        robot.mouseWheel(wheeling)
        Thread.sleep(robotTimer)
        robot.keyRelease(KeyEvent.VK_SHIFT)
        Thread.sleep(robotTimer)
        // Set second
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer * 2)
        // Begin edge
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
        Thread.sleep(robotTimer)
        // Return to first
        robot.mouseWheel(-wheeling)
        Thread.sleep(robotTimer)
        robot.keyPress(KeyEvent.VK_SHIFT)
        Thread.sleep(robotTimer)
        robot.mouseWheel(-wheeling)
        Thread.sleep(robotTimer)
        robot.keyRelease(KeyEvent.VK_SHIFT)
        Thread.sleep(robotTimer)
        // End edge
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
        Thread.sleep(robotTimer)

        Window.setSize(600, 400)
        Window.centreWindow()

        Thread.sleep(robotTimer)
        // Set third
        robot.mouseMove(
            Window.x + Window.width / 2 + Window.squareSize * 2,
            Window.y + Window.height / 2 - Window.squareSize * 2
        )
        Thread.sleep(robotTimer)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        // Replacing third
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseMove(
            Window.x + Window.width / 2 + Window.squareSize * 2,
            Window.y + Window.height / 2 + Window.squareSize * 2
        )
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        // Begin edge
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseMove(Window.x + Window.width / 2, Window.y + Window.height / 2)
        Thread.sleep(robotTimer)
        // End edge
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
        Thread.sleep(robotTimer)
        // Setting first way point
        robot.keyPress(KeyEvent.VK_META)
        Thread.sleep(robotTimer)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)

        Window.setSize(1000, 1000)
        Window.centreWindow()

        Thread.sleep(robotTimer)
        // Setting second way point
        robot.mouseMove(
            Window.x + Window.width / 2 - Window.squareSize * 2,
            Window.y + Window.height / 2 - Window.squareSize * 2
        )
        Thread.sleep(robotTimer)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        // Searching path
        robot.keyRelease(KeyEvent.VK_META)
        Thread.sleep(robotTimer)
        robot.keyPress(KeyEvent.VK_SPACE)
        Thread.sleep(robotTimer)
        robot.keyRelease(KeyEvent.VK_SPACE)
        Thread.sleep(robotTimer)

        Window.edges.size shouldBe 2

        Window.edges.forEach {
            if (it.from.name == "A" && it.to.name == "B" || it.from.name == "B" && it.to.name == "A")
                it.shortest shouldBe true
            else
                it.shortest shouldNotBe true
        }

        Thread.sleep(robotTimer)
        robot.keyPress(KeyEvent.VK_BACK_SPACE)
        Thread.sleep(robotTimer)
        robot.keyRelease(KeyEvent.VK_BACK_SPACE)
        Thread.sleep(robotTimer)

        Window.edges.size shouldBe 2

        Window.edges.forEach {
            it.shortest shouldNotBe true
        }

        // Removing B
        Thread.sleep(robotTimer)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)
        robot.mouseMove(0, 0)
        Thread.sleep(robotTimer)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(robotTimer)

        Window.edges.size shouldBe 1

        Window.edges.forEach {
            it.shortest shouldNotBe true
        }


        Thread.sleep(robotTimer * 5)
    }
}