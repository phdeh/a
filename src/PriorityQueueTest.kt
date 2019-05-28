import org.junit.Test
import org.junit.jupiter.api.Assertions.*

internal class PriorityQueueTest {
    @Test
    fun emptyQueue() {
        val pq = PriorityQueue<String>()
        pq.isEmpty() shouldBe true
        pq.isNotEmpty() shouldNotBe  true
    }

    @Test
    fun notEmptyQueue() {
        val pq = PriorityQueue<String>()
        pq.push("Test", 1.0)
        pq.isNotEmpty() shouldBe true
        pq.isEmpty() shouldNotBe  true
    }

    @Test
    fun priorityTest() {
        val pq = PriorityQueue<String>()
        pq.push("Foo", 2.0)
        pq.push("Bar", 1.0)
        pq.push("Baz", 3.0)
        pq.pop() shouldBe "Bar"
        pq.pop() shouldBe "Foo"
        pq.pop() shouldBe "Baz"
    }

    @Test
    fun startValues() {
        val pq = PriorityQueue<String>("Foo", "Bar", "Baz")
        pq.pop() shouldBe "Foo"
        pq.pop() shouldBe "Bar"
        pq.pop() shouldBe "Baz"
    }

    @Test(expected = PriorityQueue.EmptyQueueException::class)
    fun emptyQueuePop() {
        val pq = PriorityQueue<String>()
        pq.pop()
    }
}