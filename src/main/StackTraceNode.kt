package main

import java.util.*

class StackTraceNode(val name: String) {
    val edges = ArrayList<StackTraceEdge>()
    var discovered = false
    var row = -1
    var col = -1

    fun addNext(node: StackTraceNode) {
        if (edges.stream().anyMatch { it.next.name == node.name }) {
            edges.stream().filter { it.next.name == node.name }.forEach { it.occurrences++ }
        } else {
            edges.add(StackTraceEdge(node))
        }
    }

    fun getNext(name: String): StackTraceNode? {
        val first = edges.stream().filter { it.next.name == name }.findFirst()
        return if (first.isPresent) {
            first.get().next
        } else {
            null
        }
    }

    fun recursiveGetNode(name: String): StackTraceNode? {
        if (this.name == name) {
            discovered = false
            return this
        }
        discovered = true
        for (edge in edges) {
            if (!edge.next.discovered) {
                val recurse = edge.next.recursiveGetNode(name)
                if (recurse != null) {
                    discovered = false
                    return recurse
                }
            }
        }
        discovered = false
        return null
    }
}