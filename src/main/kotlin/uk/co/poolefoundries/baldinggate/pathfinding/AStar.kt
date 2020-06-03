package uk.co.poolefoundries.baldinggate.pathfinding

class AStar(val heuristic: AStarHeuristic) {

    fun getPath(graph: List<AStarNode>, startNode: AStarNode, endNode: AStarNode): MutableList<AStarNode> {
        var endInGraph = false
        if (graph.any { node -> node == endNode }) {
            endInGraph = true
        }
        val openSet = mutableListOf<AStarNode>()
        val closedSet = mutableListOf<AStarNode>()
        openSet.add(startNode)
        var workingNode: AStarNode
        while (openSet.size > 0) {
            workingNode = getLowestFScore(openSet)
            if (endInGraph) {
                if (workingNode.equals(endNode)) {
                    return reconstructPath(workingNode)
                }
            } else {
                if (getNeighborNodes(endNode, graph).any { it == workingNode }) {
                    return reconstructPath(workingNode)
                }
            }
            openSet.remove(workingNode)
            closedSet.add(workingNode)
            for (neighbor in getNeighborNodes(workingNode, graph)) {
                if (closedSet.contains(neighbor)) {
                    continue
                }
                val tentativeGScore: Double = workingNode.startToNowCost + 1.0
                if (!openSet.contains(neighbor) || neighbor.startToNowCost > tentativeGScore) {
                    neighbor.prevNode = workingNode
                    neighbor.startToNowCost = tentativeGScore
                    neighbor.nowToEndCost = neighbor.startToNowCost + heuristic.costEstimate(neighbor, endNode)
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor)
                    }
                }
            }
        }
        return mutableListOf()
    }

    private fun getLowestFScore(openSet: MutableList<AStarNode>): AStarNode {
        var lowest = openSet.first()
        for (i in 1 until openSet.size) {
            if (openSet[i].nowToEndCost < lowest.nowToEndCost) {
                lowest = openSet[i]
            }
        }
        return lowest
    }

    private fun reconstructPath(fromNode: AStarNode): MutableList<AStarNode> {
        val path = mutableListOf<AStarNode>()
        var currentNode: AStarNode? = fromNode
        while (currentNode != null) {
            path.add(currentNode)
            currentNode = currentNode.prevNode
        }
        return path
    }

    private fun getNeighborNodes(node: AStarNode, graph: List<AStarNode>): List<AStarNode> {
        val x = node.x
        val y = node.y
        return graph.filter {
            (it.x == x + 1 && it.y == y)
                    || (it.x == x && it.y == y + 1)
                    || (it.x == x - 1 && it.y == y)
                    || (it.x == x && it.y == y - 1)
        }
    }


}