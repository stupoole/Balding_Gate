package uk.co.poolefoundries.baldinggate.ai

typealias WorldState = Map<String, Any>


data class Goal(val action: Action, val priority: Double)

interface Action {
    fun getCost() : Double
    fun prerequisite(state : WorldState) : Boolean
    fun update(state: WorldState) : WorldState

    fun apply(branch: Branch) : Branch {
        val newState = update(branch.state)
        return Branch(this, newState, branch.cost + getCost())
    }
}

data class Branch(val prevAction: Action?, val state: WorldState, val cost: Double)

class ActionPlanner(val state: WorldState, actions: List<Action>, val primaryGoal: Goal){

    val bestOption : Branch?
    val openSet : List<Branch>
    init {
        val branch = Branch(null, state, 0.0)
        var openSet = actions
                .filter { it.prerequisite(state) }
                .map { it.apply(branch) }

        val goalAchievedBranches = openSet.filter { it.prevAction == primaryGoal.action }
        openSet -= goalAchievedBranches

        bestOption = goalAchievedBranches.minBy { it.cost }
        if (bestOption != null) openSet = openSet.filter { it.cost < bestOption.cost }

        this.openSet = openSet
    }

}

//fun List<Action>.applyValidActions(branch: Branch) =
//        this.filter { it.prerequisite(branch.state) }.map { it.apply(branch) }
//
//
//fun getActionPlan(state: WorldState, actions: List<Action>, primaryGoal: Goal) {
//    var openSet = actions.applyValidActions(Branch(null, state, 0.0))
//
//    return getActionPlan(actions, primaryGoal, openSet, null)
//}
//
//fun getActionPlan(actions: List<Action>, primaryGoal: Goal, openSet: List<Branch>, bestOption: Branch?){
//    val goalAchievedBranches = openSet.filter { it.prevAction == primaryGoal.action }
//    var newOpenSet = openSet - goalAchievedBranches
//
//    val newBestOption = goalAchievedBranches.minBy { it.cost } // not gonna work: needs to be concatenated with best option first
//    if (bestOption != null) newOpenSet = newOpenSet.filter { it.cost < newBestOption.cost }
//    newOpenSet = newOpenSet.flatMap { actions.applyValidActions(it) }
//
//    return getActionPlan(actions, primaryGoal, newOpenSet, newBestOption)
//}