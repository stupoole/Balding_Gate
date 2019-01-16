package uk.co.poolefoundries.baldinggate.ai

import org.junit.Test
import kotlin.test.assertEquals

class AiTest{
    @Test
    fun testing(){
        val action = GoalAction()
        val goal = Goal(action, 1.0)
        val plan = getActionPlan(emptyMap(), listOf(action), goal)
        assertEquals(1,plan?.actions?.size)
    }
    @Test
    fun fireTest(){
        val actions = listOf(MakeFire, GetTinderbox, GetWood)
        val goal = Goal(MakeFire, 1.0)
        val startState = mapOf("tinderbox" to false, "wood" to 0)
        val plan = getActionPlan(startState, actions, goal)
        assertEquals(80.0, plan?.cost)

    }
}



class GoalAction:Action{
    override val cost = 5.0

    override fun prerequisite(state: WorldState): Boolean {
        return true
    }

    override fun update(state: WorldState): WorldState {
        return state
    }

}

object MakeFire:Action{
    override val cost = 20.0

    override fun prerequisite(state: WorldState): Boolean {
        return state["tinderbox"] as Boolean && state["wood"] as Int >= 3
    }

    override fun update(state: WorldState): WorldState {
        val newState = state.toMutableMap()
        newState["wood"] = newState["wood"] as Int - 3
        return newState
    }

}

object GetWood:Action{
    override val cost = 10.0

    override fun prerequisite(state: WorldState) = true

    override fun update(state: WorldState): WorldState {
        val newState = state.toMutableMap()
        newState["wood"] = newState["wood"] as Int + 1
        return newState
    }

}

object GetTinderbox:Action {
    override val cost = 30.0

    override fun prerequisite(state: WorldState) = true

    override fun update(state: WorldState): WorldState {
        val newState = state.toMutableMap()
        newState["tinderbox"] = true
        return newState
    }

}