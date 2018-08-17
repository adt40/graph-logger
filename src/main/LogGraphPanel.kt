package main

import java.awt.*
import java.awt.geom.Line2D
import java.util.*
import javax.swing.JPanel

class LogGraphPanel(private val logGraph: StackTraceNode): JPanel() {

    var maxWidth = 0
    var maxDepth = 0

    private val phi = Math.toRadians(40.0)
    private val barb = 10

    override fun paint(g: Graphics) {
        super.paint(g)
        val grid = getGridFromGraph()
        val edges = getEdges()
        val gridSize = Math.min(width / maxWidth / 2, height / maxDepth / 2)

        for (edge in edges) {
            val g2 = g as Graphics2D
            val tip = Point(edge.second.x * gridSize * 2 + (gridSize / 2), edge.second.y * gridSize * 2 + (gridSize / 2))
            val tail = Point(edge.first.x * gridSize * 2 + (gridSize / 2), edge.first.y * gridSize * 2 + (gridSize / 2))
            drawArrow(g2, tip, tail, gridSize)
        }

        for (x in 0 until maxWidth) {
            for (y in 0 until maxDepth) {
                val name = grid[x][y]
                if (name != null) {
                    g.color = Color.CYAN
                    g.fillOval(x * gridSize * 2, y * gridSize * 2, gridSize, gridSize)
                    g.color = Color.BLACK
                    g.drawString(name, x * gridSize * 2 + (gridSize / 4), y * gridSize * 2 + (gridSize / 2))
                }
            }
        }
    }

    private fun drawArrow(g2: Graphics2D, tip: Point, tail: Point, gridSize: Int) {
        val dy = (tip.y - tail.y).toDouble()
        val dx = (tip.x - tail.x).toDouble()
        val theta = Math.atan2(dy, dx)
        val reducedTip = Point((tip.x - Math.cos(theta) * (gridSize / 2)).toInt(), (tip.y - Math.sin(theta) * (gridSize / 2)).toInt())
        g2.draw(Line2D.Double(reducedTip, tail))
        drawArrowHead(g2, reducedTip, theta)
    }

    private fun drawArrowHead(g2: Graphics2D, tip: Point, theta: Double) {
        g2.color = Color.BLACK

        var x: Double
        var y: Double
        var rho = theta + phi
        for (i in 0 until 2) {
            x = tip.x - barb * Math.cos(rho)
            y = tip.y - barb * Math.sin(rho)
            g2.draw(Line2D.Double(tip.x.toDouble(), tip.y.toDouble(), x, y))
            rho = theta - phi
        }
    }

    /**
     * output format:
     * ArrayList of edges
     * Each edge has test.a start and an end point
     *
     * MUST BE RUN AFTER getGridFromGraph() because side effects
     * This uses the calculated node and col from that method
     * Is this stupid? Yes
     * Is it noticeably faster? Should be
     * Is that worth bad coding? Undecided
     */
    private fun getEdges(): ArrayList<Pair<Point, Point>> {
        val edges = ArrayList<Pair<Point, Point>>()

        val activeNodes = arrayListOf(logGraph)
        while (activeNodes.isNotEmpty()) {
            for (node in activeNodes) {
                node.discovered = true
            }
            val calculatedNodes = ArrayList(activeNodes)
            activeNodes.clear()
            for (node in calculatedNodes) {
                for (edge in node.edges) {
                    edges.add(Pair(Point(node.col, node.row), Point(edge.next.col, edge.next.row)))
                    if (!edge.next.discovered) {
                        activeNodes.add(edge.next)
                    }
                }
            }
        }

        clearDiscoveredNodes()

        return edges
    }

    private fun setMaxWidthDepth() {
        maxDepth = 0
        val activeNodes = arrayListOf(logGraph)
        while (activeNodes.isNotEmpty()) {
            maxWidth = Math.max(maxWidth, activeNodes.size)
            for (node in activeNodes) {
                node.discovered = true
            }
            maxDepth++
            val calculatedNodes = ArrayList(activeNodes)
            activeNodes.clear()
            for (node in calculatedNodes) {
                for (edge in node.edges) {
                    if (!edge.next.discovered) {
                        activeNodes.add(edge.next)
                    }
                }
            }
        }
        clearDiscoveredNodes()
    }

    private fun getGridFromGraph(): Array<Array<String?>> {
        setMaxWidthDepth()

        val flatArrayOfNodes = ArrayList<StackTraceNode>()

        var currentRow = 0
        val activeNodes = arrayListOf(logGraph)
        while (activeNodes.isNotEmpty()) {
            var currentCol = Math.max(0, (maxWidth - activeNodes.size) / 2)
            for (node in activeNodes) {
                node.discovered = true
                node.row = currentRow
                node.col = currentCol
                currentCol++
            }
            currentRow++
            val calculatedNodes = ArrayList(activeNodes)
            flatArrayOfNodes.addAll(activeNodes)
            activeNodes.clear()
            for (node in calculatedNodes) {
                for (edge in node.edges) {
                    if (!edge.next.discovered) {
                        activeNodes.add(edge.next)
                    }
                }
            }
        }

        clearDiscoveredNodes()

        //2D array of nulls
        val grid = Array(maxWidth, { _ -> Array<String?>(maxDepth, { _ -> null }) })
        for (node in flatArrayOfNodes) {
            grid[node.col][node.row] = node.name
        }

        return grid
    }


    private fun clearDiscoveredNodes() {
        val activeNodes = arrayListOf(logGraph)
        while (activeNodes.isNotEmpty()) {
            for (node in activeNodes) {
                node.discovered = false
            }
            val calculatedNodes = ArrayList(activeNodes)
            activeNodes.clear()
            for (node in calculatedNodes) {
                for (edge in node.edges) {
                    if (edge.next.discovered) {
                        activeNodes.add(edge.next)
                    }
                }
            }
        }
    }
}