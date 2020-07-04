package uk.co.poolefoundries.baldinggate.ai.experimental

import uk.co.poolefoundries.baldinggate.ai.*
import uk.co.poolefoundries.baldinggate.systems.enemy.ai.Attack
import uk.co.poolefoundries.baldinggate.systems.enemy.ai.EndTurn
import uk.co.poolefoundries.baldinggate.systems.enemy.ai.MobInfo
import uk.co.poolefoundries.baldinggate.systems.enemy.ai.MoveTowards

/**
 * ExperimentalAi is a generic AI system that you can pass a list of actions to and it does a BFS to try and find a plan
 * to achieve the goal action. Unfortunately it has exponential time complexity so it's very slow right now. If we can
 * get this to work, it could make implementing custom AI in the game feasible.
 */
data class ExperimentalAi(var actions: List<Action>, private var primaryGoal: Goal) : Ai {
    override fun getActionPlan(state: WorldState): Plan {
        return calculateOptimalBranchToGoal(state, actions, primaryGoal)!!.actions
    }

    fun Plan.applyValidActions(branch: Branch) =
        this.getValidActions(branch.state).map { it.apply(branch) }

    fun Plan.getValidActions(worldState: WorldState) =
        this.filter { it.prerequisitesMet(worldState) }


    fun calculateOptimalBranchToGoal(state: WorldState, actions: Plan, primaryGoal: Goal): Branch? {
        val openSet = actions.applyValidActions(Branch(emptyList(), state, 0.0))
        return calculateOptimalBranchToGoal(actions, primaryGoal, openSet, null)
    }

    fun calculateOptimalBranchToGoal(actions:Plan, primaryGoal: Goal, openSet: List<Branch>, bestOption: Branch?): Branch? {
        val goalAchievedBranches = (openSet.filter { it.actions.last() == primaryGoal.action } + bestOption).filterNotNull()
        var newOpenSet = openSet - goalAchievedBranches


        val newBestOption = goalAchievedBranches.minBy { it.cost }
        if (newBestOption != null) newOpenSet = newOpenSet.filter { it.cost < newBestOption.cost }


        newOpenSet = newOpenSet.flatMap { actions.applyValidActions(it) }
        if (bestOption != null) {
            newOpenSet = newOpenSet.filter { it.cost > bestOption.cost }
        }

        newOpenSet = newOpenSet.groupBy { it.state }.map { it.value.minBy { branch -> branch.cost } }.filterNotNull()

        return if (newOpenSet.isNotEmpty()) {
            calculateOptimalBranchToGoal(actions, primaryGoal, newOpenSet, newBestOption)
        } else {
            newBestOption
        }
    }

    companion object {
        // TODO(jpoole): This should probably not live here
        fun new(players: Collection<MobInfo>, enemies: Collection<MobInfo>, goalAction: Action) : ExperimentalAi {
            val goal = Goal(goalAction, 1.0)
            return ExperimentalAi(actions(players, enemies, goal), goal)
        }

        private fun actions(players: Collection<MobInfo>, enemies: Collection<MobInfo>, goal: Goal): List<Action> {
            val attackPlayerActions = players.flatMap { player -> enemies.map { enemy ->
                Attack(
                    enemy.id,
                    player.id
                )
            } }
            val moveTowardsPlayerActions =
                players.flatMap { player -> enemies.map { enemy ->
                    MoveTowards(
                        enemy.id,
                        player.id
                    )
                } }

            return listOf(goal.action, EndTurn)
                .union(attackPlayerActions)
                .union(moveTowardsPlayerActions)
                .toList()
        }
    }
}