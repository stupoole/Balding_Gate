package uk.co.poolefoundries.baldinggate.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.ENTER
import com.badlogic.gdx.Input.Keys.TAB
import com.badlogic.gdx.InputAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame

class PlayerInputHandler(val game: BaldingGateGame) : InputAdapter() {

    private var lastX = 0f
    private var lastY = 0f
    // todo get list of valid actions that aren't movement and display on UI

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            // TODO replace player movement with camera movement
            ENTER -> game.endTurn()
            TAB -> game.nextPlayer()

        }

        // TODO: (from Appliction.kt) Add a turn based entity system in order to make all updates to game at the end
        // of a turn
        // TODO: make this such that all systems that should act are acting here

        return true
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        // TODO: attack skeletons with certain button presses

        lastX = x.toFloat()
        lastY = y.toFloat()
        // Button: 0-> left, 1-> right, 2-> middle, 4 -> mouse forward, 3 -> mouse back
        when (button) {
            0 -> {

                game.leftClick(x,y)
                return true
            }
            1 -> {


                game.rightClick(x,y)
                return true
            }

        }
        return false
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        game.camera.translate(-(x - lastX), y - lastY)

        lastX = x.toFloat()
        lastY = y.toFloat()
        // TODO replace touchDragged with wasd. Rotate with q and e?
        return true
    }


}