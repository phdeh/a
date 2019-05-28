import org.junit.Test
import java.lang.Exception

internal class GraphTest {
    @Test
    fun emptyGraphTest() {
        val graph = Graph.build { }
        graph.size shouldBe 0
    }

    @Test
    fun correctValues() {
        val graph = Graph.build {
            val a by it(1.0, -1.0)

            a
        }
        graph["a"].name shouldBe "a"
        graph["a"].toString() shouldBe "a"
        graph["a"].x shouldBe 1.0
        graph["a"].y shouldBe -1.0
    }
    @Test
    fun explicitNaming() {
        val graph = Graph.build {
            val n1 by it("Moscow", 1.0, -1.0)
            val n2 by it("Saint-Petersburg", 1.0, -1.0)

            n1 - n2
        }
        ("Moscow" in graph) shouldBe true
        ("n1" in graph) shouldNotBe true
    }

    @Test
    fun duplicatingName() {
        val graph = Graph.build {
            val node1 by it("Node", 1.0, -1.0)
            val node2 by it("Node", 1.0, -1.0)
            val node3 by it("Node", 1.0, -1.0)

            node1 - node2 - node3
        }
        ("Node" in graph) shouldBe true
        ("Node_2" in graph) shouldBe true
        ("Node_3" in graph) shouldBe true
        ("Node_4" in graph) shouldNotBe true
    }

    @Test
    fun containsNameA() {
        val graph = Graph.build {
            val a by it(1.0, -1.0)

            a
        }
        ("a" in graph) shouldBe true
    }

    @Test
    fun aConnectedToB() {
        val graph = Graph.build {
            val a by it(1.0, 1.0)
            val b by it(-1.0, -1.0)

            a - b
        }
        graph["a"] hasEdgeTo graph["b"] shouldBe true
    }

    @Test
    fun commutativeConnection() {
        val graph = Graph.build {
            val a by it(1.0, 1.0)
            val b by it(-1.0, -1.0)

            b - a
        }
        graph["a"] hasEdgeTo graph["b"] shouldBe true
    }

    @Test
    fun aNotConnectedToB() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(-1.0, 0.0)
            val c by it(0.0, 1.0)

            a - c - b
        }
        graph["a"] hasEdgeTo graph["b"] shouldBe false
    }

    @Test
    fun indices() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(-1.0, 0.0)
            val c by it(0.0, 1.0)

            a - c - b
        }
        graph[0] shouldBe graph["a"]
        graph[1] shouldBe graph["b"]
        graph[2] shouldBe graph["c"]
        graph["a"].ordinal shouldBe 0
        graph["b"].ordinal shouldBe 1
        graph["c"].ordinal shouldBe 2
    }

    @Test
    fun backtracking() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)

            a
        }
        graph["a"].graph shouldBe graph
        graph["b"].graph shouldNotBe graph
        graph["c"].graph shouldNotBe graph
        graph["b"].graph shouldNotBe graph["c"].graph
    }

    @Test
    fun dDoesntExists() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(-1.0, 0.0)
            val c by it(0.0, 1.0)

            a - c - b
        }
        (graph["d"] !in graph) shouldBe true
    }

    @Test
    fun distanceTest() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(-1.0, 0.0)

            a - b
        }
        (graph["a"] distanceTo graph["b"]) shouldBe 2.0
    }

    @Test
    fun impossibleDistanceTest() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)

            a
        }
        (graph["a"] distanceTo graph["d"]).isNaN() shouldBe true
    }

    @Test
    fun edgesAreCorrect() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(-1.0, 0.0)
            val c by it(0.0, 1.0)
            val d by it(0.0, 2.0)

            a - b - c - a; b - d - c
        }
        val edges = graph["a"].edgesTo
        (graph["b"] in edges) shouldBe true
        (graph["c"] in edges) shouldBe true
        (graph["d"] in edges) shouldNotBe true
        (graph["e"] in edges) shouldNotBe true
    }

    @Test
    fun nonExistingVerticesAreNotEqual() {
        val graph = Graph.build {}
        (graph["a"] != graph["b"]) shouldBe true
    }

    @Test
    fun sizeOfGraph() {
        val graph = Graph.build {
            val a by it(1.0, 0.0)
            val b by it(-1.0, 0.0)
            val c by it(0.0, 1.0)

            a - b - c - a
        }
        graph.size shouldBe 3
    }
}