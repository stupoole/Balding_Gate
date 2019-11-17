package uk.co.poolefoundries.baldinggate.skeleton

import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.ai.actions.MoveAction
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition

val POSITION_KEY = "position"
val PLAYER_POSITION_KEY = "player"

val goal = Goal(MoveToPlayer, 1.0)


// TODO: add goal: attack player
object MoveToPlayer : Action {
    override val cost = 1.0

    override fun prerequisite(state: WorldState): Boolean {
        val pos = state.getPosition(POSITION_KEY)
        val playerPos = state.getPosition(PLAYER_POSITION_KEY)

        return pos.distance(playerPos) <= 1.0
    }

    override fun update(state: WorldState): WorldState {
        return state.withValue(POSITION_KEY, state.getValue(PLAYER_POSITION_KEY))
    }

}

object SkeletonAI {
    val actions = listOf(
        MoveAction(POSITION_KEY, UP),
        MoveAction(POSITION_KEY, DOWN),
        MoveAction(POSITION_KEY, LEFT),
        MoveAction(POSITION_KEY, RIGHT),
        MoveToPlayer
    )

    fun getNewPosition(pos : PositionComponent, playerPos : PositionComponent) : PositionComponent {
        val worldState : WorldState = mapOf(PLAYER_POSITION_KEY to playerPos, POSITION_KEY to pos)
        val plan = getActionPlan(worldState, actions, goal)!!

        val action = plan.actions.first()
        val newState = action.update(worldState)

        return newState.getPosition(POSITION_KEY)
    }
}