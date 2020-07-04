package uk.co.poolefoundries.baldinggate.ai

interface Ai {
    fun getActionPlan(state: WorldState): Plan
}

typealias Plan = List<Action>
data class Branch(val actions: Plan, val state: WorldState, val cost: Double)
data class Goal(val action: Action, val priority: Double)

interface Action {
    // How much time this action should take in arbitrary units
    fun cost(state: WorldState): Double

    // Checks to see if for the given world state, that this action can be performed
    fun prerequisitesMet(state: WorldState): Boolean

    // Update the world state as if this action was performed
    fun update(state: WorldState): WorldState

    // Produce a new branch where this action has been performed
    fun apply(branch: Branch): Branch {
        val newState = update(branch.state)
        return Branch(branch.actions + this, newState, branch.cost + cost(branch.state))
    }

    override fun toString(): String

}
