package uk.co.poolefoundries.baldinggate.entitysystems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import uk.co.poolefoundries.baldinggate.core.*

object AnimationSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()

    private fun (Entity).toAnimation(): AnimationComponent? {
        return if (getComponent(AnimationComponent::class.java).isFinished) {
            null
        } else {
            getComponent(AnimationComponent::class.java)
        }
    }


    override fun update(delta: Float) {
        val pendingAnimations =
            players().mapNotNull { it.toAnimation() } + enemies().mapNotNull { it.toAnimation() }
        pendingAnimations.forEach { it.animationStep(delta) }
    }


    fun addAnimation(entity: Entity, positions: List<PositionComponent>) {
        entity.add(AnimationComponent(entity, positions))
    }

}