package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.core.*

//val UP = PositionComponent(0, 1)
//val DOWN = PositionComponent(0, -1)
//val LEFT = PositionComponent(-1, 0)
//val RIGHT = PositionComponent(1, 0)

object PlayerSystem : EntitySystem() {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    private var walls = ImmutableArray(Array<Entity>())
    private var players = ImmutableArray(Array<Entity>())
    private var skeletons = ImmutableArray(Array<Entity>())

    // todo replace the wasd controls with text input options
    // cycle through players to ask what to do by printing all possible actions
    private fun move(direction: PositionComponent) {
        players.forEach { player ->
            val pos = positionMapper.get(player)
            // make a list of valid actions, display them and then, on button press,
            val newPos = PositionComponent(
                pos.x + direction.x,
                pos.y + direction.y
            )

            if (walls.none { positionMapper.get(it) == newPos } && skeletons.none { positionMapper.get(it) == newPos }) {
                player.add(newPos)
            } else if (skeletons.any { positionMapper.get(it) == newPos }) {
                // TODO Attack
            } else {
                println("something blocking movement")
            }
        }

    }


    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        walls = engine.getEntitiesFor(Family.all(WallComponent::class.java, PositionComponent::class.java).get())
        players = engine.getEntitiesFor(
            Family.all(
                PlayerComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
        skeletons = engine.getEntitiesFor(
            Family.all(
                SkeletonComponent::class.java,
                PositionComponent::class.java,
                StatsComponent::class.java
            ).get()
        )
    }


//    fun moveUp() {
//        move(UP)
//    }
//
//    fun moveDown() {
//        move(DOWN)
//    }
//
//    fun moveLeft() {
//        move(LEFT)
//    }
//
//    fun moveRight() {
//        move(RIGHT)
//    }

    fun FuckAll() {

    }
}