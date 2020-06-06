package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import jdk.nashorn.internal.runtime.SharedPropertyMap
import uk.co.poolefoundries.baldinggate.core.*
import uk.co.poolefoundries.baldinggate.systems.player.PlayerTurnSystem


data class SelectedEntity(
    var entity: Entity? = null,
    var position: PositionComponent? = null,
    var actionPoints: Int = 0,
    var speed: Int = 0,
    var isPlayer: Boolean = false
) {
    fun clear() {
        this.entity?.add(ColorComponent(Color.WHITE))
        entity = null
        position = null
        actionPoints = 0
        speed = 0
        isPlayer = false
    }
}

data class Line(val startX: Float, val startY: Float, val endX: Float, val endY: Float)

object EntitySelectionSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private fun players() = engine.getEntitiesFor(playerFamily).toList()
    private fun enemies() = engine.getEntitiesFor(enemyFamily).toList()
    var selectionBorders = Pair(listOf<Line>(), Color.CLEAR)
    var movementBorders = Pair(listOf<List<Line>>(), listOf<Color>())
    private const val tileSize = 25F //TODO get this from the game engine/level somehow
    private var selectedEntity = SelectedEntity()
    private fun (Entity).toPosition(): PositionComponent {
        return getComponent(PositionComponent::class.java)
    }

    private fun (Entity).toStats(): Stats {
        return getComponent(StatsComponent::class.java).stats
    }


    // Selects next player with AP or deselects
    fun nextPlayer(): Boolean {
        val activePlayers = players().filter { it.toStats().currentAP > 0 }
        return if (activePlayers.isNotEmpty()) {
            val current = activePlayers.indexOf(selectedEntity.entity)
            // loops to select next player
            val next = (current + 1) % activePlayers.size
            selectEntity(activePlayers[next], true)
        } else {
            deselectEntity()
        }
    }

    // Will update whatever system we use to keep track of selected entity and return whether it was successful
    // should also update highlighted tiles
    private fun selectEntity(entity: Entity, isPlayer: Boolean): Boolean {
        selectedEntity = SelectedEntity(
            entity,
            entity.toPosition(),
            entity.toStats().currentAP,
            entity.toStats().speed,
            isPlayer
        )
        return true
    }


    // Will clear selected entity and call updates to highlighted tiles
    private fun deselectEntity(): Boolean {
        selectedEntity.clear()
        return true
    }

    // find the game tile position and if the player is selected gets the player system to do the correct action
    // Returns whether an action was carried out
    fun actAt(x: Int, y: Int): Boolean {
        val playerSystem = engine.getSystem(PlayerTurnSystem::class.java)
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val gamePos = cameraSystem.unproject(x, y)
        val targetPos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())
        if (players().contains(selectedEntity.entity)) {
            val acted = playerSystem.determineAction(selectedEntity.entity!!, targetPos)
            reselectEntity()
            return acted
        }
        return false
    }

    fun reselectEntity() {
        val entity = selectedEntity.entity!!
        if (selectedEntity.isPlayer) {
            selectEntity(entity, true)
            selectionBorders = Pair(calculateSelectionBorders(), Theme.BLUE)
            movementBorders = Pair(calculateMovementBorders().reversed(), listOf(Theme.VIOLET, Theme.MAGENTA).reversed())
        } else {
            selectEntity(entity, false)
            selectionBorders = Pair(calculateSelectionBorders(), Theme.RED)
            movementBorders = Pair(calculateMovementBorders().reversed(), listOf(Theme.ORANGE, Theme.YELLOW).reversed())
        }
    }

    // Will select entity at given position if valid target exists. Returns whether an entity was selected
    fun selectEntityAt(x: Int, y: Int): Boolean {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val gamePos = cameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())

        val selectedPlayers = players().filter { it.toPosition() == tilePos }
        if (selectedPlayers.isNotEmpty()) {
            selectEntity(selectedPlayers.first(), true)
            selectionBorders = Pair(calculateSelectionBorders(), Theme.BLUE)
            movementBorders =
                Pair(calculateMovementBorders().reversed(), listOf(Theme.VIOLET, Theme.MAGENTA).reversed())
            return true
        }

        val selectedEnemies = enemies().filter { it.toPosition() == tilePos }
        if (selectedEnemies.isNotEmpty()) {
            selectEntity(selectedEnemies.first(), false)
            selectionBorders = Pair(calculateSelectionBorders(), Theme.RED)
            movementBorders = Pair(calculateMovementBorders().reversed(), listOf(Theme.ORANGE, Theme.YELLOW).reversed())
            return true
        }
        selectedEntity.clear()
        selectionBorders = Pair(listOf<Line>(), Color())
        movementBorders = Pair(listOf<List<Line>>(), listOf<Color>())
        return false
    }

    fun getSelectedEntity(): Entity? {
        return selectedEntity.entity
    }

    fun getSelectedPlayerStats(): Stats? {
        return if (selectedEntity.isPlayer)
            selectedEntity.entity?.toStats()
        else null
    }

    fun getSelectedPlayerPos(): PositionComponent? {
        return if (selectedEntity.isPlayer)
            selectedEntity.entity?.toPosition()
        else null
    }

    fun calculateMovementBorders(): List<List<Line>> {

        val borders = mutableListOf<List<Line>>()
        val ap = selectedEntity.entity!!.toStats().currentAP
        for (action in 1..ap) {
            val edgeTiles = PathfinderSystem.findSpread(selectedEntity.position!!, selectedEntity.speed * action)
                .filter { (it.left || it.right || it.top || it.bottom) }
            val lines = mutableListOf<Line>()
            edgeTiles.forEach { tile ->

                val x = tile.x.toFloat()
                val y = tile.y.toFloat()
                if (tile.right){
                    lines.add(Line((x + 1) * tileSize, (y) * tileSize, (x + 1) * tileSize, (y + 1) * tileSize))
                }
                if (tile.left) {
                    lines.add(Line((x) * tileSize, (y) * tileSize, (x) * tileSize, (y + 1) * tileSize))
                }
                if (tile.bottom) {
                    lines.add(Line((x) * tileSize, (y) * tileSize, (x + 1) * tileSize, (y) * tileSize))
                }
                if (tile.top) {
                    lines.add(Line((x) * tileSize, (y + 1) * tileSize, (x + 1) * tileSize, (y + 1) * tileSize))
                }
            }
            borders.add(lines.toList())
        }
        return borders.toList()
    }

    fun calculateSelectionBorders(): List<Line> {

        val lines = mutableListOf<Line>()
        val x = selectedEntity.entity!!.toPosition().x.toFloat()
        val y = selectedEntity.entity!!.toPosition().y.toFloat()

        lines.add(Line(x * tileSize, y * tileSize, (x + 1) * tileSize, y * tileSize))
        lines.add(Line(x * tileSize, y * tileSize, (x) * tileSize, (y + 1) * tileSize))
        lines.add(Line((x + 1) * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize))
        lines.add(Line(x * tileSize, (y + 1) * tileSize, (x + 1) * tileSize, (y + 1) * tileSize))

        return lines.toList()


    }
}