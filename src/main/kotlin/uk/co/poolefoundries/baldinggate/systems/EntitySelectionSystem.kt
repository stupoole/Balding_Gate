package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
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
    var selectionBorders = listOf<Line>()
    var movementBorders = listOf<List<Line>>()
    fun movementColors(step: Int) = if (selectedEntity.isPlayer) {
        when (step) {
            0 -> Theme.VIOLET
            else -> Theme.MAGENTA
        }
    } else {
        when (step) {
            0 -> Theme.ORANGE
            else -> Theme.YELLOW
        }
    }

    fun selectColors() = if (selectedEntity.isPlayer) Theme.BLUE else Theme.RED
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
        val activePlayers = players().filter { it.toStats().currentAP > 0 && it.toStats().hitPoints > 0 }
        return if (activePlayers.isNotEmpty()) {
            val current = activePlayers.indexOf(selectedEntity.entity)
            val next = (current + 1) % activePlayers.size
            updateSelectedEntity(activePlayers[next])
            recalculateBorders()
            true
        } else {
            deselectEntity()
        }
    }

    // Will update whatever system we use to keep track of selected entity and return whether it was successful
    // should also update highlighted tiles
    private fun updateSelectedEntity(entity: Entity): Boolean {
        selectedEntity = SelectedEntity(
            entity,
            entity.toPosition(),
            entity.toStats().currentAP,
            entity.toStats().speed,
            players().contains(entity)
        )

        return true
    }

    // Will clear selected entity and call updates to highlighted tiles
    private fun deselectEntity(): Boolean {
        selectedEntity.clear()
        selectionBorders = listOf()
        movementBorders = listOf()
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
            recalculateBorders()
            return acted
        }
        return false
    }

    private fun recalculateBorders() {
        selectedEntity.entity?.let { updateSelectedEntity(it) }
        selectionBorders = calculateSelectionBorders().reversed()
        movementBorders = calculateMovementBorders().reversed()
    }

    // Will select entity at given position if valid target exists. Returns whether an entity was selected
    fun selectEntityAt(x: Int, y: Int): Boolean {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val gamePos = cameraSystem.unproject(x, y)
        val tilePos = PositionComponent((gamePos.x / tileSize).toInt(), (gamePos.y / tileSize).toInt())

        val selectedMobs = (players() + enemies()).filter { it.toPosition() == tilePos && it.toStats().hitPoints > 0 }
        if (selectedMobs.isNotEmpty()) {
            updateSelectedEntity(selectedMobs.first())
            recalculateBorders()
            return true
        }
        deselectEntity()
        return false
    }

    fun getSelectedPlayerStats(): Stats? {
        return if (selectedEntity.isPlayer)
            selectedEntity.entity?.toStats()
        else null
    }

    private fun calculateMovementBorders(): List<List<Line>> {
        val borders = mutableListOf<List<Line>>()
        val ap = selectedEntity.entity!!.toStats().currentAP
        for (action in 1..ap) {
            val edgeTiles = PathfinderSystem.findSpread(selectedEntity.position!!, selectedEntity.speed * action)
                .filter { (it.left || it.right || it.top || it.bottom) }
            val lines = mutableListOf<Line>()
            edgeTiles.forEach { tile ->

                val x = tile.x.toFloat()
                val y = tile.y.toFloat()
                if (tile.right) {
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

    private fun calculateSelectionBorders(): List<Line> {

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