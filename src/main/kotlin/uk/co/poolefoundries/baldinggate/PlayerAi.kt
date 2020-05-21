package uk.co.poolefoundries.baldinggate

import uk.co.poolefoundries.baldinggate.ai.Action
import uk.co.poolefoundries.baldinggate.ai.WorldState
import uk.co.poolefoundries.baldinggate.ai.actions.MoveAction
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition
import uk.co.poolefoundries.baldinggate.ai.withValue
import uk.co.poolefoundries.baldinggate.skeleton.PLAYER_POSITION_KEY
import uk.co.poolefoundries.baldinggate.skeleton.POSITION_KEY

object FuckAll : Action {
    override val cost = 1.0

    override fun prerequisite(state: WorldState): Boolean {
        return true
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

    override fun toString(): String {
        return "FuckAll"
    }
}



