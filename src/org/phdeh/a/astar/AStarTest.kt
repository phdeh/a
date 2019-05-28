package org.phdeh.a.astar

import org.junit.Test

internal class AStarTest {

    @Test
    fun findPath() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(0.0, 1.0)
            val c by it(-1.0, 0.0)

            a - b - c
        }

        AStar.findPath(graph["a"], graph["c"]) shouldBe listOf(graph["a"], graph["b"], graph["c"])
    }

    @Test
    fun findShortestPath() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(0.0, 2.0)
            val c by it(0.0, 1.0)
            val d by it(-1.0, 0.0)

            a - b - d - c - a
        }

        AStar.findPath(graph["a"], graph["d"]) shouldBe listOf(graph["a"], graph["c"], graph["d"])
    }

    @Test
    fun wayDoesntExist() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(0.0, 2.0)
            val c by it(0.0, 1.0)
            val d by it(-1.0, 0.0)

            a - b; c - d
        }

        AStar.findPath(graph["a"], graph["d"]) shouldBe listOf<Graph.Vertex>()
    }

    @Test
    fun fromAToA() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(0.0, 2.0)
            val c by it(0.0, 1.0)
            val d by it(-1.0, 0.0)

            a - b - d - c - a
        }

        AStar.findPath(graph["a"], graph["a"]) shouldBe listOf(graph["a"])
    }

    @Test(expected = AStar.VerticeBelongToDifferentGraphsException::class)
    fun differentGraphs() {
        val graph1 = Graph.build {
            val a by it(1.0, 0.0)

            a
        }
        val graph2 = Graph.build {
            val a by it(1.0, 0.0)

            a
        }

        AStar.findPath(graph1["a"], graph2["a"])
    }

    @Test(expected = AStar.VertexDoesntExistException::class)
    fun nonExistingVerticesFrom() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)

            a
        }

        AStar.findPath(graph["d"], graph["a"])
    }

    @Test(expected = AStar.VertexDoesntExistException::class)
    fun nonExistingVerticesTo() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)

            a
        }

        AStar.findPath(graph["a"], graph["d"])
    }

    @Test(expected = AStar.VertexDoesntExistException::class)
    fun nonExistingVerticesBothFromAndTo() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)

            a
        }

        AStar.findPath(graph["c"], graph["d"])
    }
}