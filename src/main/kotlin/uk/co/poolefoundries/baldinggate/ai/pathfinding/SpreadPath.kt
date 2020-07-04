package uk.co.poolefoundries.baldinggate.ai.pathfinding

object SpreadPath {

    fun getMovementBorder(graph: List<SpreadNode>, startNode: SpreadNode, speed: Int): MutableList<SpreadNode> {
        val openSet = graph.toMutableList()
        var closedSet = mutableListOf(startNode)
        for (progress in 0 until speed) {
            val workingSet = mutableListOf<SpreadNode>()
            closedSet.forEach { workingNode ->
                val steps = getNeighborNodes(workingNode, graph)
                workingSet.addAll(steps.filter { !closedSet.contains(it) })
            }
            closedSet = closedSet.union(workingSet).toMutableList()
            openSet -= workingSet

        }
        return getConnections(closedSet)
    }


    private fun getNeighborNodes(node: SpreadNode, graph: List<SpreadNode>): MutableList<SpreadNode> {
        val x = node.x
        val y = node.y
        return graph.filter {
            (it.x == x + 1 && it.y == y)
                    || (it.x == x && it.y == y + 1)
                    || (it.x == x - 1 && it.y == y)
                    || (it.x == x && it.y == y - 1)
        }.toMutableList()
    }

    private fun getConnections(closedSet: MutableList<SpreadNode>): MutableList<SpreadNode> {
        closedSet.forEach { node ->
            if (closedSet.find { it.x == node.x && it.y == node.y + 1 } == null) {
                node.top = true
            }
            if (closedSet.find { it.x == node.x && it.y == node.y - 1 } == null) {
                node.bottom = true
            }
            if (closedSet.find { it.x == node.x - 1 && it.y == node.y } == null) {
                node.left = true
            }
            if (closedSet.find { it.x == node.x + 1 && it.y == node.y } == null) {
                node.right = true
            }
        }
        return closedSet

    }

}

