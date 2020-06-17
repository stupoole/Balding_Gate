package uk.co.poolefoundries.baldinggate.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor

interface GameInputHandler {
    fun endTurn()
    fun nextPlayer()
    fun pause()
    fun leftClick(x: Int, y: Int)
    fun rightClick(x: Int, y: Int)
    fun dragCamera(deltaX: Float, deltaY: Float)
    fun zoom(amount: Int)
}

object RawInputHandler : InputProcessor {

    private var lastX = 0f
    private var lastY = 0f
    private val listener = GameInputProcessor

    override fun keyDown(keycode: Int): Boolean {
        return when (keycode) {
            Keys.ENTER -> {
                listener.endTurn()
                true
            }
            Keys.TAB -> {
                listener.nextPlayer()
                true
            }
            Keys.ESCAPE -> {
                listener.pause()
                true
            }
            else -> false
        }
    }

    override fun scrolled(amount: Int): Boolean {
        listener.zoom(amount)
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Buttons.LEFT -> {
                listener.leftClick(x, y)
                true
            }
            Buttons.RIGHT -> {
                listener.rightClick(x, y)
                true
            }
            Buttons.MIDDLE -> {
                lastX = x.toFloat()
                lastY = y.toFloat()
                true
            }
            else -> false
        }

    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        return if ( Gdx.input.isButtonPressed(Buttons.MIDDLE)) {
            listener.dragCamera(x - lastX, y - lastY)
            lastX = x.toFloat()
            lastY = y.toFloat()
            true
        } else {
            false
        }
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Buttons.MIDDLE -> {
                true
            }
            else -> false
        }

    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }


}