package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.utils.Array
import javafx.geometry.Pos
import kotlin.random.Random


data class Roll(val die : List<Int>, val mod : Int) {
    fun roll() = die.map { Random.nextInt(it) }.sum() + mod
}

data class StatsComponent(val vitality : Int, var hitpoints : Int, val attack : Roll) : Component

fun createSkeleton(x : Int, y : Int): Entity {
    val entity = Entity()

    entity.add(StatsComponent(10, 10, Roll(listOf(4), 1)))
    entity.add(PositionComponent(x, y))
    entity.add(VisualComponent(TextureRenderable(Resources.skeleton())))

    return entity
}

class SkeletonSystem() : IntervalIteratingSystem(Family.all(StatsComponent::class.java, PositionComponent::class.java).get(), 1f) {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    private var walls = ImmutableArray(Array<Entity>())

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        walls = engine.getEntitiesFor(Family.all(WallComponent::class.java, PositionComponent::class.java).get())
    }

    override fun processEntity(skeleton: Entity) {
        val pos = positionMapper.get(skeleton)

        val newPos = PositionComponent(pos.x + Random.nextInt(-1, 2), pos.y + Random.nextInt(-1, 2))

        if(walls.none { positionMapper.get(it) == newPos }){
            skeleton.add(newPos)
        }
    }
}