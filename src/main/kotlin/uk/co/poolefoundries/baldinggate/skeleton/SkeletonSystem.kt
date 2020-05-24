package uk.co.poolefoundries.baldinggate.skeleton

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.*
import uk.co.poolefoundries.baldinggate.ai.actions.getPosition
import uk.co.poolefoundries.baldinggate.ai.actions.getStats
import uk.co.poolefoundries.baldinggate.model.PlayerComponent
import uk.co.poolefoundries.baldinggate.model.SkeletonComponent
import uk.co.poolefoundries.baldinggate.model.Stats

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
        // TODO: stop player and skeleton from overlapping when player and skeleton move to same location maybe add a move validator
        skeletons.forEach { skeleton ->
            val playerPos: PositionComponent
            val playerStats: Stats
            val player: Entity
            player = players.first()

            // TODO: convert players in to a simplified representation of a player
            playerPos = positionMapper.get(player)
            playerStats = statsMapper.get(player).stats
            val pos = positionMapper.get(skeleton)
            val stats = statsMapper.get(skeleton).stats
//            SkeletonAI.getNewState(players)

            val actionPlan = SkeletonAI.getPlan(pos, playerPos, stats, playerStats)
            when (actionPlan.actions.first()) {
                is MoveTowardsPlayer -> {
                    //TODO add collision detection
                    skeleton.add(pos + pos.direction(playerPos))
                }
                is AttackPlayer -> {
                    player.add(StatsComponent(playerStats.copy(hitPoints = playerStats.hitPoints - stats.attack.roll())))
                    println("Hitpoints: "+statsMapper.get(player).stats.hitPoints)
                }
                is Win -> {
                    player.remove(VisualComponent::class.java)
                    println("HAHAHAHAHA YOU DIED!")
                }

                //            val newPos = newState.getPosition(SKELETON_POSITION_KEY)
                //            if (walls.none { positionMapper.get(it) == newPos } && players.none { positionMapper.get(it) == newPos }) {
                //                skeleton.add(newPos)
                //            }

                //            // TODO make roll using it.stats.Roll.roll()
                //            // TODO make it much simlper to deal damage to the hitpoints instead of reconstructing or copying a stats object
                //            val playerHP = statsMapper.get(player).stats.hitPoints
                //            val newHP = newState.getStats(PLAYER_STATS_KEY).hitPoints
                //            if (playerHP!=newHP){
                //                player.add(StatsComponent(newState.getStats(PLAYER_STATS_KEY)))
                //
                //                println("Damage = " + (playerHP-newHP).toString() + " Hitpoints: " + playerHP)
                //                player.add(StatsComponent(newState.getStats(PLAYER_STATS_KEY)))
                //                if (newHP<=0){
                //                    player.remove(VisualComponent::class.java)
                //                }
                //            }
            }

//            val newPos = newState.getPosition(SKELETON_POSITION_KEY)
//            if (walls.none { positionMapper.get(it) == newPos } && players.none { positionMapper.get(it) == newPos }) {
//                skeleton.add(newPos)
//            }

//            // TODO make roll using it.stats.Roll.roll()
//            // TODO make it much simlper to deal damage to the hitpoints instead of reconstructing or copying a stats object
//            val playerHP = statsMapper.get(player).stats.hitPoints
//            val newHP = newState.getStats(PLAYER_STATS_KEY).hitPoints
//            if (playerHP!=newHP){
//                player.add(StatsComponent(newState.getStats(PLAYER_STATS_KEY)))
//
//                println("Damage = " + (playerHP-newHP).toString() + " Hitpoints: " + playerHP)
//                player.add(StatsComponent(newState.getStats(PLAYER_STATS_KEY)))

//            }
        }


    }
}