package uk.co.poolefoundries.baldinggate.skeleton

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.PositionComponent
import uk.co.poolefoundries.baldinggate.StatsComponent
import uk.co.poolefoundries.baldinggate.WallComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import kotlin.random.Random

object SkeletonSystem : EntitySystem() {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    private var walls = ImmutableArray(Array<Entity>())
    private var skeletons = ImmutableArray(Array<Entity>())

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        walls = engine.getEntitiesFor(
            Family.all(WallComponent::class.java, PositionComponent::class.java).get()
        )
        skeletons = engine.getEntitiesFor(
            Family.all(
                SkeletonComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
    }

    fun act() {
        skeletons.forEach { skeleton ->
            val pos = positionMapper.get(skeleton)
            // TODO: Add way to get player position.
            val newPos = SkeletonAI.getNewPosition(pos, PositionComponent(5, 5))

            if (walls.none { positionMapper.get(it) == newPos }) {
                skeleton.add(newPos)
            }
        }
    }
}