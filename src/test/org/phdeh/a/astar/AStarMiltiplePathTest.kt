package org.phdeh.a.astar

import org.junit.Ignore
import org.junit.Test

internal class AStarMiltiplePathTest {
    @Test
    fun multiplePaths() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(0.0, 1.0)
            val c by it(-1.0, 0.0)
            val d by it(0.0, -1.0)

            a - b - c - d - a
        }

        AStar.findAllPaths(graph["a"], graph["c"]) shouldBe listOf(
            listOf(graph["a"], graph["b"], graph["c"]),
            listOf(graph["a"], graph["d"], graph["c"])
        )
    }

    @Ignore("Not implemented yet") @Test
    fun pathsMadeOnTheRun() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(0.0, 1.0)
            val c by it(-1.0, 0.0)
            val d by it(0.0, -1.0)

            a - b; c - d
        }

        AStar.findAllPaths(graph["a"], graph["c"]) shouldBe listOf(
            listOf(graph["a"], graph["e"], graph["c"])
        )
    }

}