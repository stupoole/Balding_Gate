package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import java.awt.ScrollPane


object CameraSystem : EntitySystem() {
    var camera = OrthographicCamera()
    var viewport = ScreenViewport(camera)
    val batch = SpriteBatch()
    val stage = Stage(viewport, batch)

    override fun addedToEngine(engine: Engine?) {
        viewport.apply()
        camera.update()
    }

//    override fun update(deltaTime: Float) {
////        stage.draw()
//    }

    fun resize(width:Int, height:Int){
        viewport.update(width, height, true)
    }

    fun pan(deltaX: Float, deltaY: Float) {
        camera.translate(camera.zoom * -(deltaX), camera.zoom * (deltaY))
    }

    fun zoom(amount: Int){
        camera.zoom += 0.1F * amount.toFloat()
        if (camera.zoom < 0.2) {
            camera.zoom = 0.2F
        }
    }

    fun unproject(x:Int, y:Int):Vector2{
        val vect = camera.unproject(Vector3(x.toFloat(),y.toFloat(),0F))
        return Vector2(vect.x, vect.y)
    }

    fun newStage(){
        stage.clear()
    }

    fun addActorToStage(actor:Actor){
        stage.addActor(actor)
    }

    fun setScrollFocus(actor:Actor){
        stage.scrollFocus = actor
    }

    fun renderStage(delta: Float){
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.act()
        stage.draw()
    }

    fun dispose(){
        stage.dispose()
        batch.dispose()
    }

}