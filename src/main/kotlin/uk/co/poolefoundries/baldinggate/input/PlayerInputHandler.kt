package uk.co.poolefoundries.baldinggate.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.InputAdapter
import uk.co.poolefoundries.baldinggate.core.*


class PlayerInputHandler(val game: BaldingGateGame) : InputAdapter() {

    private var lastX = 0f
    private var lastY = 0f
    // todo get list of valid actions that aren't movement and display on UI

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            // TODO replace player movement with camera movement
            ENTER -> game.endTurn()
            TAB -> game.nextPlayer()
            W -> game.cameraMoveDirection += PAN_UP
            A -> game.cameraMoveDirection += PAN_LEFT
            S -> game.cameraMoveDirection += PAN_DOWN
            D -> game.cameraMoveDirection += PAN_RIGHT
            ESCAPE -> game.pauseMenu()
        }

        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            // TODO replace player movement with camera movement
            W -> game.cameraMoveDirection -= PAN_UP
            A -> game.cameraMoveDirection -= PAN_LEFT
            S -> game.cameraMoveDirection -= PAN_DOWN
            D -> game.cameraMoveDirection -= PAN_RIGHT
        }

        return true
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        // TODO: attack skeletons with certain button presses
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



}