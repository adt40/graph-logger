package main

import javax.swing.JFrame
import javax.swing.WindowConstants

class Logger {
    companion object {

        private const val stackStartIndex = 3

        private var logGraph: StackTraceNode? = null

        var printClass = true
        var printMethod = true
        var filterTypes = arrayListOf(LogType.INFO, LogType.DEBUG, LogType.ERROR)
        var textPosition = 40

        fun info(message: String) {
            log(message, LogType.INFO)
        }
        fun info(name: String, value: Any) {
            log(getMessage(name, value), LogType.INFO)
        }

        fun error(message: String) {
            log(message, LogType.ERROR)
        }
        fun error(name: String, value: Any) {
            log(getMessage(name, value), LogType.ERROR)
        }

        fun debug(message: String) {
            log(message, LogType.DEBUG)
        }
        fun debug(name: String, value: Any) {
            log(getMessage(name, value), LogType.DEBUG)
        }

        fun printLogGraph() {
            if (logGraph != null) {
                recursivePrintLog("", logGraph!!)
            }
        }

        private fun recursivePrintLog(line: String, currentNode: StackTraceNode) {
            if (currentNode.edges.size == 0) {
                println(line + currentNode.name)
            } else {
                for(edge in currentNode.edges) {
                    recursivePrintLog(line + currentNode.name + " ", edge.next)
                }
            }
        }

        fun displayLogGraph() {
            if (logGraph != null) {
                val panel = LogGraphPanel(logGraph!!)
                val frame = JFrame("Log Graph")
                frame.contentPane.add(panel)
                frame.setSize(400, 400)
                frame.isVisible = true
                frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            }
        }

        private fun log(message: String, type: LogType) {
            val stackTrace = Thread.currentThread().stackTrace
            updateLogGraph(stackTrace)
            printToLog(message, type, stackTrace)
        }

        private fun printToLog(message: String, type: LogType, stackTrace: Array<StackTraceElement>) {
            if (filterTypes.contains(type)) {
                val method = stackTrace[stackStartIndex]
                val name = getMethodName(method)
                val space = getSpace(type, name)
                println(name + space + "<" + type.name + "> " + message)
            }
        }

        private fun getSpace(type: LogType, methodName: String): String {
            val numSpaces = textPosition - (type.name.length + methodName.length + 1)
            var space = ""
            for (i in 0 until numSpaces) {
                space += if (space.length % 2 == 0) {
                    " "
                } else {
                    "-"
                }
            }
            return space
        }

        private fun updateLogGraph(stackTrace: Array<StackTraceElement>) {
            if (logGraph == null) {
                logGraph = StackTraceNode(getMethodName(stackTrace.last()))
            }

            var index = stackTrace.lastIndex - 1
            var curr = logGraph

            while(index >= stackStartIndex) {
                val name = getMethodName(stackTrace[index])
                var node = logGraph!!.recursiveGetNode(name)
                if (node == null) {
                    node = StackTraceNode(name)
                }
                curr!!.addNext(node)
                curr = curr.getNext(name)!!
                index--
            }
        }

        private fun getMessage(name: String, value: Any): String {
            return name + " = " + value.toString()
        }

        private fun getMethodName(method: StackTraceElement):String {
            if (printClass && !printMethod) {
                return method.className
            } else if (!printClass && printMethod) {
                return method.methodName
            } else if (printClass && printMethod) {
                return method.className + "::" + method.methodName
            } else {
                return ""
            }
        }
    }
}