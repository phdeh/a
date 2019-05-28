import kotlin.reflect.KProperty

class Graph private constructor(builder: GraphBuilder) {

    private val vertices: Map<String, Vertex>
    private val verticesIndexed: List<Vertex>

    operator fun get(name: String) = vertices[name] ?: EMPTY_VERTEX

    operator fun get(index: Int) = if (index in this) verticesIndexed[index] else EMPTY_VERTEX

    val size get() = vertices.size

    operator fun contains(name: String) = vertices.containsKey(name)

    operator fun contains(vertex: Vertex) = vertices.containsValue(vertex)

    operator fun contains(index: Int) = index in 0..(vertices.size - 1)

    override fun equals(other: Any?): Boolean {
        return other != null &&
                other is Graph &&
                this !== EMPTY_GRAPH &&
                other !== EMPTY_GRAPH &&
                this === other
    }

    override fun hashCode(): Int = vertices.hashCode()

    data class Vertex internal constructor(
        val graph: Graph,
        val name: String,
        val x: Double,
        val y: Double,
        val ordinal: Int,
        private val hasEdgesTo: MutableList<Vertex>
    ) {
        val edgesTo by lazy { List(hasEdgesTo.size, { hasEdgesTo[it] }).toSet() }

        infix fun hasEdgeTo(other: Vertex?) = this !== EMPTY_VERTEX &&
                other != null &&
                other !== EMPTY_VERTEX &&
                other in edgesTo

        override fun equals(other: Any?): Boolean {
            return this !== EMPTY_VERTEX && other !== EMPTY_VERTEX && super.equals(other)
        }

        override fun hashCode(): Int {
            return name.hashCode() xor x.hashCode() xor y.hashCode()
        }

        infix fun distanceTo(other: Vertex): Double =
            if (this === EMPTY_VERTEX || other === EMPTY_VERTEX)
                Double.NaN
            else
                Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))

        override fun toString(): String = name
    }

    init {
        synchronized(builder) {
            val vb = List(builder.vertices.size) { builder.vertices[it].vertexBuilder }
            val relations = List(vb.size) { mutableListOf<Vertex>() }
            val names = mutableSetOf<String>()
            val verticesList = List(vb.size) {
                val b = vb[it]
                if (b.name !in names) {
                    names += b.name
                    Vertex(this, b.name, b.x, b.y, it, relations[it])
                } else {
                    var i = 2
                    while ("${b.name}_$i" in names)
                        i++
                    names += "${b.name}_$i"
                    Vertex(this, "${b.name}_$i", b.x, b.y, it, relations[it])
                }
            }
            relations.forEachIndexed { it, list ->
                vb[it].currentlyEdgesTo.forEach { e ->
                    list += verticesList[vb.indexOf(e)]
                }
            }
            val verticesMap = mutableMapOf<String, Vertex>()
            verticesList.forEach {
                verticesMap[it.name] = it
            }
            this.verticesIndexed = verticesList
            this.vertices = verticesMap
        }
    }

    class GraphBuilder {

        internal val vertices = mutableListOf<VertexDelegate>()

        operator fun invoke(x: Double, y: Double): VertexDelegate {
            val vd = VertexDelegate(null, x, y)
            vertices += vd
            return vd
        }

        operator fun invoke(name: String, x: Double, y: Double): VertexDelegate {
            val vd = VertexDelegate(name, x, y)
            vertices += vd
            return vd
        }

        class VertexBuilder(val x: Double, val y: Double) {
            private var actualName: String? = null
            var name
                get() = actualName ?: "UNDEFINED"
                set(value) {
                    if (actualName == null)
                        actualName = value
                }

            private val actuallyEdgesTo = mutableSetOf<VertexBuilder>()

            val currentlyEdgesTo get() = actuallyEdgesTo.toSet()

            operator fun minus(other: VertexBuilder): VertexBuilder {
                hasEdgeTo(other)
                return other
            }

            fun hasEdgeTo(other: VertexBuilder) {
                synchronized(this) {
                    actuallyEdgesTo += other
                }
                synchronized(other) {
                    other.actuallyEdgesTo += this
                }
            }

        }

        class VertexDelegate(name: String?, x: Double, y: Double) {
            val vertexBuilder = VertexBuilder(x, y).let {
                if (name != null)
                    it.name = name
                it
            }

            operator fun getValue(thisRef: Any?, property: KProperty<*>): VertexBuilder {
                vertexBuilder.name = property.name
                return vertexBuilder
            }
        }
    }

    companion object {
        fun build(action: (GraphBuilder) -> Unit): Graph {
            val builder = GraphBuilder()
            action(builder)
            return Graph(builder)
        }

        val EMPTY_GRAPH = Graph.build { }
        val EMPTY_VERTEX = Vertex(
            EMPTY_GRAPH,
            "EMPTY_VERTEX",
            Double.NaN,
            Double.NaN,
            -1,
            mutableListOf()
        )
    }


}