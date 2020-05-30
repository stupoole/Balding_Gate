package uk.co.poolefoundries.baldinggate.pathfinding

class Astar(val heuristic: AStarHeuristic) {

    fun getPath(graph: List<List<AstarNode>>, startNode: AstarNode, endNode: AstarNode):MutableList<AstarNode>? {
        val openSet = mutableListOf<AstarNode>()
        val closedSet = mutableListOf<AstarNode>()
        openSet.add(startNode)
        var workingNode : AstarNode
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
                val tentativeGScore: Double = workingNode.gScore + neighbor.passingCost
                if (!openSet.contains(neighbor) || neighbor.gScore > tentativeGScore) {
                    neighbor.prevNode = workingNode
                    neighbor.gScore = tentativeGScore
                    neighbor.fScore = neighbor.gScore + heuristic.costEstimate(neighbor, endNode)
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor)
                    }
                }
            }
        }
        return null
    }

    private fun getLowestFScore(openSet: MutableList<AstarNode>): AstarNode {
        var lowest = openSet.first()
        for (i in 1 until openSet.size) {
            if (openSet[i].fScore < lowest.fScore) {
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

    private fun getNeighborNodes(node: AstarNode, graph: List<List<AstarNode>>): MutableList<AstarNode> {
        val neighbors = mutableListOf<AstarNode>()
        val x = node.x
        val y = node.y
        addToArrayIfInBounds(neighbors, x + 1, y, graph)
        addToArrayIfInBounds(neighbors, x - 1, y, graph)
        addToArrayIfInBounds(neighbors, x, y + 1, graph)
        addToArrayIfInBounds(neighbors, x, y - 1, graph)
        return neighbors
    }

    private fun addToArrayIfInBounds(
        neighbors: MutableList<AstarNode>, x: Int, y: Int, graph: List<List<AstarNode>>
    ) {
        if (!(x < 0 || x >= graph.size) && !(y < 0 || y >= graph[x].size)) {
            neighbors.add(graph[x][y])
        }
    }
}