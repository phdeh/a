package org.phdeh.a.gui

import java.util.*

class NameLobby {
    private var letter = 0
    private var number = 0
    private val stack = Stack<String>()

    fun getName(): String {
        if (stack.isNotEmpty())
            return stack.pop()
        val cl = (letter + 'A'.toInt()).toChar()
        val name = if (number == 0)
            "$cl"
        else
            "$cl$number"

        if (cl == 'Z') {
            letter = 0
            number++
        } else
            letter++

        return name
    }

    fun returnName(name: String) {
        stack.push(name)
    }
}