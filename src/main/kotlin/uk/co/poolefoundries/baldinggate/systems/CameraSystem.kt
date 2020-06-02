package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport


object CameraSystem : EntitySystem() {
    val gameCamera = OrthographicCamera()
    var gameViewport = ScreenViewport(gameCamera)
    val stageCamera = OrthographicCamera()
    var stageViewport = ScreenViewport(stageCamera)
    val batch = SpriteBatch()
    val stage = Stage(stageViewport, batch)

    override fun addedToEngine(engine: Engine?) {
        stageCamera.update()
    }

    fun switchToStage(){
        stage.clear()
        stageCamera.update()
    }

    fun switchToGame(){
        stage.clear()
        gameViewport.apply()
        gameCamera.update()
    }


    fun resize(width:Int, height:Int){
        gameViewport.update(width, height, true)
        stageViewport.update(width, height, true)
    }

    fun pan(deltaX: Float, deltaY: Float) {
        gameCamera.translate(gameCamera.zoom * -(deltaX), gameCamera.zoom * (deltaY))
    }

    fun zoom(amount: Int){
        gameCamera.zoom += 0.1F * amount.toFloat()
        if (gameCamera.zoom < 0.2) {
            gameCamera.zoom = 0.2F
        }
    }

    fun unproject(x:Int, y:Int):Vector2{
        val vect = gameCamera.unproject(Vector3(x.toFloat(),y.toFloat(),0F))
        return Vector2(vect.x, vect.y)
    }

    fun newStage(){
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