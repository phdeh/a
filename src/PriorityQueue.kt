import java.lang.RuntimeException
import java.util.*

class PriorityQueue<T>(vararg ts: T) {
    class EmptyQueueException : RuntimeException()
    private val mp = mutableListOf<Double>()
    private val mt = mutableListOf<T>()

    init {
        ts.forEach {
            push(it, 0.0)
        }
    }

    fun push(t: T, priority: Double) {
        synchronized(this) {
            mp.add(priority)
            mt.add(t)
        }
    }

    fun isEmpty() = mp.isEmpty()
    fun isNotEmpty() = !isEmpty()
    fun pop(): T {
        if (isEmpty())
            throw EmptyQueueException()
        synchronized(this) {
            var max = mp[0]
            var index = 0
            var i = 0
            do {
                if (mp[i] < max) {
                    index = i
                    max = mp[i]
                }
            } while (++i < mp.size)
            mp.removeAt(index)
            val result = mt[index]
            mt.removeAt(index)
            return result
        }
    }
}