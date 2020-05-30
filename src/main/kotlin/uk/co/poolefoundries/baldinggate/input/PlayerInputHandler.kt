package uk.co.poolefoundries.baldinggate.input

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame


class PlayerInputHandler(val game: BaldingGateGame) : InputAdapter() {

    private var lastX = 0f
    private var lastY = 0f
    private var middle = false
    // todo get list of valid actions that aren't movement and display on UI

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            // TODO replace player movement with camera movement
            Keys.ENTER -> game.endTurn()
            Keys.TAB -> game.nextPlayer()

        }
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        game.camera.zoom += 0.1F * amount.toFloat()
        if (game.camera.zoom < 0.2) {
            game.camera.zoom = 0.2F
        }
        return true
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        // TODO: attack skeletons with certain button presses
        when (button) {
            Buttons.LEFT -> {
                game.leftClick(x, y)
                return true
            }
            Buttons.RIGHT -> {
                game.rightClick(x, y)
                return true
            }
            Buttons.MIDDLE -> {
                lastX = x.toFloat()
                lastY = y.toFloat()
                middle = true
                return true
            }
        }
        return false
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        if (middle) {
            game.camera.translate(game.camera.zoom * -(x - lastX), game.camera.zoom * (y - lastY))
            lastX = x.toFloat()
            lastY = y.toFloat()
            return true
        }
        return false
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Buttons.MIDDLE -> {
                middle = false
                true
            }
            else -> false
        }

    }


}