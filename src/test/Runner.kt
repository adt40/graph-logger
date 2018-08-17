package test

import main.Logger

fun main(args: Array<String>) {
    Logger.printClass = false
    Logger.printMethod = true
    Logger.textPosition = 20

    val value = 2
    val a = a(value)
    val c = c(a)
    val d = d(c)

    Logger.printLogGraph()
    Logger.displayLogGraph()
}


fun a(value: Int): Int {
    return b(value) + 1
}

fun b(value: Int): Int {
    Logger.info(value.toString())
    return value + 4
}

fun c(value: Int): Int {
    Logger.debug(value.toString())
    return value - 6
}

fun d(value: Int): Int {
    return f(e(value) * 2)
}

fun e(value: Int): Int {
    return b(value) - h(value)
}

fun f(value: Int): Int {
    return g(value)
}

fun g(value: Int): Int {
    return e(value)
}

fun h(value: Int): Int {
    return c(value - 2) + 1
}