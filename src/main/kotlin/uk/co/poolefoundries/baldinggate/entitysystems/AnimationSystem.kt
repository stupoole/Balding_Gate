package uk.co.poolefoundries.baldinggate.entitysystems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.skeleton.MobInfo

object AnimationSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()


    private fun (Entity).toMobInfo() : MobInfo {
        val stats = getComponent(StatsComponent::class.java).stats
        val pos = getComponent(PositionComponent::class.java)
        val id = getComponent(IdComponent::class.java)
        return MobInfo(id.id, pos.copy(), stats.copy())
    }

    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()


}