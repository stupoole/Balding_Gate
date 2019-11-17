package uk.co.poolefoundries.baldinggate

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera


/**
 * PanHandler is a class that controls and allows movement of the camera on click and drag actions
 */
class PanHandler(private val camera: OrthographicCamera) : InputAdapter() {
    var lastX = 0f
    var lastY = 0f

    // Stores starting location of camera
    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        lastX = x.toFloat()
        lastY = y.toFloat()
        return true
    }

    // Moves camera to new location whilst being dragged
    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        camera.translate(-(x - lastX), y - lastY)

        lastX = x.toFloat()
        lastY = y.toFloat()

        return true
    }

}
