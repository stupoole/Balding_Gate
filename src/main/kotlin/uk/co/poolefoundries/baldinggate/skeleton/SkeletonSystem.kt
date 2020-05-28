package uk.co.poolefoundries.baldinggate.skeleton

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.core.*


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
            var playerPos: PositionComponent
            var playerStats: Stats
            val player: Entity
            player = players.first()

            // TODO: convert players in to a simplified representation of a player

            // TODO replace this action points system to do one action per skeleton each, render then do the next?



            var stats = statsMapper.get(skeleton).stats
            val speed = stats.speed
//            SkeletonAI.getNewState(players)
            for (action in 0 until stats.currentAP) {
                playerPos = positionMapper.get(player)
                playerStats = statsMapper.get(player).stats
                val pos = positionMapper.get(skeleton)
                val actionPlan = SkeletonAI.getPlan(pos, playerPos, stats, playerStats)
                when (actionPlan.actions.first()) {
                    is MoveTowardsPlayer -> {
                        //TODO add collision detection
                        if (pos.distance(playerPos) <= speed) {
                            skeleton.add(pos + pos.direction(playerPos))
                        } else {
                            for (step in 0 until speed) {
                                val tempPos = skeleton.getComponent(PositionComponent::class.java)
                                skeleton.add(tempPos + tempPos.direction(playerPos))
                            }
                        }
                    }
                    is AttackPlayer -> {
                        player.add(
                            StatsComponent(
                                statsMapper.get(player).stats.copy(
                                    hitPoints = playerStats.hitPoints - stats.attack.roll()
                                )
                            )
                        )
                        println("Hitpoints: " + statsMapper.get(player).stats.hitPoints)
                    }
                    is Win -> {
                        player.remove(VisualComponent::class.java)
                        println("HAHAHAHAHA YOU DIED!")
                    }
                }

                skeleton.add(StatsComponent(stats.copy(currentAP = stats.currentAP-1)))
                stats = statsMapper.get(skeleton).stats

            }


        }


    }
}