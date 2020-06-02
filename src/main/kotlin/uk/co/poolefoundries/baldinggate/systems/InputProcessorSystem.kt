package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ScreenAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.input.InputHandler
import uk.co.poolefoundries.baldinggate.screens.PauseMenuScreen
import uk.co.poolefoundries.baldinggate.systems.player.PlayerTurnSystem

object InputProcessorSystem : EntitySystem(), InputHandler {

    private var lastX = 0F
    private var lastY = 0F
    private var middle = false

    override fun endTurn() {
        PlayerTurnSystem.endTurn()
    }

    override fun nextPlayer() {
        EntitySelectionSystem.nextPlayer()
    }

    override fun pause() {
        BaldingGateGame.screen = PauseMenuScreen(BaldingGateGame, BaldingGateGame.screen as ScreenAdapter)
    }


    override fun dragCamera(deltaX: Float, deltaY: Float) {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        cameraSystem.pan(deltaX, deltaY)

    }

    override fun zoom(amount: Int) {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        cameraSystem.zoom(amount)
    }

    override fun leftClick(x: Int, y: Int) {
        val selectionSystem = engine.getSystem(EntitySelectionSystem::class.java)
        selectionSystem.selectEntityAt(x, y)
    }

    override fun rightClick(x: Int, y: Int) {
        val selectionSystem = engine.getSystem(EntitySelectionSystem::class.java)
        selectionSystem.actAt(x, y)
    }


}