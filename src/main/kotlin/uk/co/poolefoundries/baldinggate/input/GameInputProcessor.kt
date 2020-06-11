package uk.co.poolefoundries.baldinggate.input

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ScreenAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.screens.PauseMenuScreen
import uk.co.poolefoundries.baldinggate.systems.CameraSystem
import uk.co.poolefoundries.baldinggate.systems.EntitySelectionSystem
import uk.co.poolefoundries.baldinggate.systems.player.PlayerTurnSystem

object GameInputProcessor : EntitySystem(), GameInputHandler {

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
        CameraSystem.pan(deltaX, deltaY)

    }

    override fun zoom(amount: Int) {
        CameraSystem.zoom(amount)
    }

    override fun leftClick(x: Int, y: Int) {
        EntitySelectionSystem.selectEntityAt(x, y)
    }

    override fun rightClick(x: Int, y: Int) {
        EntitySelectionSystem.actAt(x, y)
    }


}