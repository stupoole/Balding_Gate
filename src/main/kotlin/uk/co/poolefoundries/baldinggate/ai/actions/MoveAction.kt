package uk.co.poolefoundries.baldinggate.ai.actions

import uk.co.poolefoundries.baldinggate.PositionComponent
import uk.co.poolefoundries.baldinggate.ai.Action
import uk.co.poolefoundries.baldinggate.ai.WorldState
import uk.co.poolefoundries.baldinggate.ai.withValue

class MoveAction(private val positionKey: String, private val direction: PositionComponent) : Action {
    override val cost = 1.0

    override fun prerequisite(state: WorldState): Boolean {
        return state.hasPosition(positionKey)
    }

    override fun update(state: WorldState): WorldState {
        val pos = state.getPosition(positionKey)
        val newPos = PositionComponent(pos.x + direction.x, pos.y + direction.y)
        return state.withValue(positionKey, newPos)
    }

    override fun toString(): String {
        return "MoveAction"
    }

}

fun (WorldState).hasPosition(key: String) = this.containsKey(key) && this.getValue(key) is PositionComponent

fun (WorldState).getPosition(key: String) = this.getValue(key) as PositionComponent
