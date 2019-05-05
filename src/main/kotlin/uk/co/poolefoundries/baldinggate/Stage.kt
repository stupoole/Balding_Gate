package uk.co.poolefoundries.baldinggate

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport



class PanHandler(private val camera: OrthographicCamera) : InputAdapter() {
    var lastX = 0f
    var lastY = 0f


    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        lastX = x.toFloat()
        lastY = y.toFloat()
        return true
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        camera.translate(-(x-lastX), y-lastY)

        lastX = x.toFloat()
        lastY = y.toFloat()

        return true
    }

}
