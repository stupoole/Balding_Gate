package uk.co.poolefoundries.baldinggate.input

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ScreenAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.screens.PauseMenuScreen
import uk.co.poolefoundries.baldinggate.systems.CameraSystem
import uk.co.poolefoundries.baldinggate.systems.editor.LevelEditorSystem

object LevelEditorInputProcessor : EntitySystem(), EditorInputHandler {

    override fun pause() {
        BaldingGateGame.screen = PauseMenuScreen(BaldingGateGame, BaldingGateGame.screen as ScreenAdapter)
    }

    override fun leftClick(x: Int, y: Int) {
        CameraSystem.unfocus()
        LevelEditorSystem.place(x, y)
    }

    override fun rightClick(x: Int, y: Int) {
        CameraSystem.unfocus()
        LevelEditorSystem.remove(x,y)
    }

    override fun dragCamera(deltaX: Float, deltaY: Float) {
        CameraSystem.unfocus()
        CameraSystem.pan(deltaX, deltaY)
    }

    override fun zoom(amount: Int) {
        CameraSystem.zoom(amount)
    }

}
