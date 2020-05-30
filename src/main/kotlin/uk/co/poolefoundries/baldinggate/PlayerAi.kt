package uk.co.poolefoundries.baldinggate

import uk.co.poolefoundries.baldinggate.ai.Action
import uk.co.poolefoundries.baldinggate.ai.Goal
import uk.co.poolefoundries.baldinggate.ai.WorldState
import uk.co.poolefoundries.baldinggate.ai.actions.getStats
import uk.co.poolefoundries.baldinggate.skeleton.AttackPlayer

const val SKELETON_STATS_KEY = "skellystats"

val goal = Goal(Win, 1.0)

object Win : Action {
    override fun cost(state: WorldState): Double {
        return 1.0
    }

    override fun prerequisite(state: WorldState): Boolean {
        //TODO implement player AI
        return state.getStats(SKELETON_STATS_KEY).hitPoints <= 0
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

    override fun toString(): String {
        return "Win"
    }

}



