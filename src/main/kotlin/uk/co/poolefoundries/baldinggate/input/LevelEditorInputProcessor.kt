package uk.co.poolefoundries.baldinggate.input

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ScreenAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.screens.PauseMenuScreen
import uk.co.poolefoundries.baldinggate.systems.CameraSystem

object LevelEditorInputProcessor : EntitySystem(), EditorInputHandler {

    override fun pause() {
        // TODO("Update the pause screen to have save/load level options for the level editor")
        BaldingGateGame.screen = PauseMenuScreen(BaldingGateGame, BaldingGateGame.screen as ScreenAdapter)
    }

    override fun leftClick(x: Int, y: Int) {
        CameraSystem.unfocus()
    }

    override fun rightClick(x: Int, y: Int) {
        CameraSystem.unfocus()
    }

    override fun dragCamera(deltaX: Float, deltaY: Float) {
        CameraSystem.unfocus()
        CameraSystem.pan(deltaX, deltaY)
    }

    override fun zoom(amount: Int) {
        CameraSystem.zoom(amount)
    }

}
