package org.phdeh.a.astar

import java.lang.Exception

infix fun <A, B> A.shouldBe(other: B) {
    if (this != other)
        throw Exception("$this should be $other")
}

infix fun <A, B> A.shouldNotBe(other: B) {
    if (this == other)
        throw Exception("$this should be $other")
}