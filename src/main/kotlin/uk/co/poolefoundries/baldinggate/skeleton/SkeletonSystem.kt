package uk.co.poolefoundries.baldinggate.skeleton

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.Resources.get
import uk.co.poolefoundries.baldinggate.ai.WorldState
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition
import uk.co.poolefoundries.baldinggate.ai.actions.getStats
import uk.co.poolefoundries.baldinggate.model.PlayerComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import uk.co.poolefoundries.baldinggate.model.Stats
import kotlin.random.Random

object SkeletonSystem : EntitySystem() {
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val statsMapper = ComponentMapper.getFor(StatsComponent::class.java)

    private var walls = ImmutableArray(Array<Entity>())
    private var skeletons = ImmutableArray(Array<Entity>())
    private var players = ImmutableArray(Array<Entity>())


    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        walls = engine.getEntitiesFor(
            Family.all(WallComponent::class.java, PositionComponent::class.java).get()
        )
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

    fun act() {
        // TODO: somehow handle multiple players

        //TODO: stop player and skeleton from overlapping when player and skeleton move to same location maybe add a move validator
        skeletons.forEach { skeleton ->
            val playerPos: PositionComponent
            val playerStats: StatsComponent
            val player: Entity
            player = players.first()
            playerPos = positionMapper.get(player)
            playerStats = statsMapper.get(player)
            val pos = positionMapper.get(skeleton)
            val stats = statsMapper.get(skeleton)
            val newState = SkeletonAI.getNewState(pos, playerPos, stats, playerStats)
            val newPos = newState.getPosition(POSITION_KEY)
            if (walls.none { positionMapper.get(it) == newPos } && players.none { positionMapper.get(it) == newPos }) {
                skeleton.add(newPos)
            }
            player.add(newState.getStats(PLAYER_STATS_KEY))
            val playerHP = statsMapper.get(player).stats.hitPoints
            println("Hitpoints: " + playerHP)
            if (playerHP<=0){
                player.remove(VisualComponent::class.java)
            }
        }


    }
}