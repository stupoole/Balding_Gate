package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.utils.Array
import javafx.geometry.Pos
import uk.co.poolefoundries.baldinggate.model.PlayerComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import uk.co.poolefoundries.baldinggate.model.Stats
import kotlin.random.Random


data class Roll(val die: List<Int>, val mod: Int) {
    fun roll() = die.map { Random.nextInt(it) }.sum() + mod
}

data class StatsComponent(val stats: Stats) : Component


//fun createSkeleton(x: Int, y: Int): Entity {
//    val entity = Entity()
//
//    entity.add(StatsComponent())
//    entity.add(PositionComponent(x, y))
//    entity.add(VisualComponent(TextureRenderable(Resources.skeleton())))
//
//    return entity
//}


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

            val newPos = PositionComponent(pos.x + Random.nextInt(-1, 2), pos.y + Random.nextInt(-1, 2))

            if (walls.none { positionMapper.get(it) == newPos }) {
                skeleton.add(newPos)
            }
        }
    }
}

