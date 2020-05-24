package uk.co.poolefoundries.baldinggate.ai.actions

import uk.co.poolefoundries.baldinggate.PositionComponent
import uk.co.poolefoundries.baldinggate.StatsComponent
import uk.co.poolefoundries.baldinggate.ai.Action
import uk.co.poolefoundries.baldinggate.ai.WorldState
import uk.co.poolefoundries.baldinggate.ai.withValue
import uk.co.poolefoundries.baldinggate.model.Stats

class MoveAction(private val positionKey: String, private val direction: PositionComponent) : Action {
    override fun cost(state: WorldState) = 1.0

    override fun prerequisite(state: WorldState): Boolean {
        // TODO: make the check that the destination is safe to move in to
        return state.hasPosition(positionKey)
    }

    override fun update(state: WorldState): WorldState {
        val pos = state.getPosition(positionKey)
        val newPos = PositionComponent(pos.x + direction.x, pos.y + direction.y)
        return state.withValue(positionKey, newPos)
    }

    override fun toString(): String {
        return "MoveAction$positionKey"
    }

}

fun (WorldState).hasPosition(key: String) = this.containsKey(key) && this.getValue(key) is PositionComponent

fun (WorldState).getPosition(key: String) = this.getValue(key) as PositionComponent

fun (WorldState).hasStats(key: String) = this.containsKey(key) && this.getValue(key) is StatsComponent

fun (WorldState).getStats(key: String) = this.getValue(key) as Stats

