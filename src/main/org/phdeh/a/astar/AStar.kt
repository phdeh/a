package org.phdeh.a.astar

import java.lang.RuntimeException
import java.util.*

object AStar {
    class VerticeBelongToDifferentGraphsException(msg: String) : RuntimeException(msg)
    class VertexDoesntExistException(msg: String) : RuntimeException(msg)

    fun findPath(from: Graph.Vertex, to: Graph.Vertex): List<Graph.Vertex> {
        if (from === Graph.NON_EXISTENT_VERTEX && to === Graph.NON_EXISTENT_VERTEX)
            throw VertexDoesntExistException("both from and to")
        if (from === Graph.NON_EXISTENT_VERTEX)
            throw VertexDoesntExistException("from")
        if (to === Graph.NON_EXISTENT_VERTEX)
            throw VertexDoesntExistException("to")
        if (from.graph != to.graph)
            throw VerticeBelongToDifferentGraphsException("$from and $to")
        if (from == to)
            return listOf(to)
        val graph = from.graph

        val frontier = PriorityQueue(from)
        val cameFrom = Array<Graph.Vertex?>(graph.size) { null }
        val handled = BitSet(graph.size)
        val costSoFar = DoubleArray(graph.size) { 0.0 }

        handled[from.ordinal] = true

        fun directDist(v: Graph.Vertex) = Math.sqrt(
            Math.pow((to.y - v.y), 2.0) +
                    Math.pow((to.x - v.x), 2.0)
        )

        var last = from
        while (frontier.isNotEmpty()) {
            val current = frontier.pop()
            last = current
            if (current == to)
                break

            current.edgesTo.forEach {
                val newCost = costSoFar[current.ordinal] + it.distanceTo(current)
                if (!handled[it.ordinal] || newCost < costSoFar[it.ordinal]) {
                    handled[it.ordinal] = true
                    costSoFar[it.ordinal] = newCost
                    cameFrom[it.ordinal] = current
                    val priority = newCost + directDist(it)
                    frontier.push(it, priority)
                }
            }
        }

        if (last == to) {
            val ml = mutableListOf<Graph.Vertex>()
            var curr: Graph.Vertex? = last
            while (curr != null) {
                ml.add(curr)
                curr = cameFrom[curr.ordinal]
            }
            return ml.reversed()
        }
        return listOf()
    }

    fun findAllPaths(from: Graph.Vertex, to: Graph.Vertex): List<List<Graph.Vertex>> {
        val paths = mutableSetOf(findPath(from, to))
        if (from != to && paths.isNotEmpty()) {
            var minLength = 0.0
            paths.first().forEachPair { a, b ->
                minLength += a distanceTo b
            }
            fun depthFirst(current: Graph.Vertex, visited: List<Graph.Vertex>, length: Double) {
                if (current == to && length <= minLength + 0.001)
                    paths += visited
                if (length <= minLength + 0.001)
                    current.edgesTo.forEach {
                        if (it !in visited)
                            depthFirst(it, visited + it, length + current.distanceTo(it))
                    }
            }
            depthFirst(from, listOf(from), 0.0)
        }
        return paths.toList()
    }
}