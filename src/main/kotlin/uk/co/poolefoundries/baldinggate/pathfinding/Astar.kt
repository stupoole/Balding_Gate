package uk.co.poolefoundries.baldinggate.pathfinding

class Astar(val heuristic: AStarHeuristic) {

    // TODO remove any passing costs
    // TODO create a graph somehow
    //


    fun getPath(graph: List<AstarNode>, startNode: AstarNode, endNode: AstarNode): MutableList<AstarNode> {
        val openSet = mutableListOf<AstarNode>()
        val closedSet = mutableListOf<AstarNode>()
        openSet.add(startNode)
        var workingNode: AstarNode
        while (openSet.size > 0) {
            workingNode = getLowestFScore(openSet)
            if (workingNode.equals(endNode)) {
                return reconstructPath(workingNode)
            }
            openSet.remove(workingNode)
            closedSet.add(workingNode)
            for (neighbor in getNeighborNodes(workingNode, graph)) {
                if (closedSet.contains(neighbor)) {
                    continue
                }
                val tentativeGScore: Double = workingNode.startToNowCost + 1
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

    private fun getLowestFScore(openSet: MutableList<AstarNode>): AstarNode {
        var lowest = openSet.first()
        for (i in 1 until openSet.size) {
            if (openSet[i].nowToEndCost < lowest.nowToEndCost) {
                lowest = openSet[i]
            }
        }
        return lowest
    }

    private fun reconstructPath(fromNode: AstarNode): MutableList<AstarNode> {
        val path = mutableListOf<AstarNode>()
        var currentNode: AstarNode? = fromNode
        while (currentNode != null) {
            path.add(currentNode)
            currentNode = currentNode.prevNode
        }
        return path
    }

    private fun getNeighborNodes(node: AstarNode, graph: List<AstarNode>): List<AstarNode> {
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