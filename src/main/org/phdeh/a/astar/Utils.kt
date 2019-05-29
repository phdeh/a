package org.phdeh.a.astar


fun<K,T : Iterable<K>> T.forEachPair(action: (a: K, b: K) -> Unit) {
    var a = null as K?
    var b = null as K?
    val iter = this.iterator()
    while (iter.hasNext()) {
        a = b
        b = iter.next()
        if (a != null && b != null)
            action(a, b)
    }
}