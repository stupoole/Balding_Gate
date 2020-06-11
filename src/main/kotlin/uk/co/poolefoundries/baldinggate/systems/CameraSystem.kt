package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport


object CameraSystem : EntitySystem() {
    val gameCamera = OrthographicCamera()
    private var gameViewport = ScreenViewport(gameCamera)
    private val menuCamera = OrthographicCamera()
    private var menuViewport = ScreenViewport(menuCamera)
    private val HUDCamera = OrthographicCamera()
    private var HUDViewport = ScreenViewport(HUDCamera)
    private val shapeRenderer = ShapeRenderer()

    val batch = SpriteBatch()
    val menuStage = Stage(menuViewport, batch)
    val HUDStage = Stage(HUDViewport, batch)

    fun addActorToStage(actor: Actor) {
        menuStage.addActor(actor)
        menuStage.act()
        menuStage.draw()
    }


    fun addActorToHUD(actor: Actor) {
        HUDStage.addActor(actor)
    }


    override fun addedToEngine(engine: Engine?) {
        menuCamera.update()
        gameCamera.position.set(Vector3(gameViewport.worldHeight / 4F, gameViewport.worldWidth / 4F, 0F))
        gameCamera.update()

    }


    fun dispose() {
        menuStage.dispose()
        batch.dispose()
    }


    fun drawHUD() {
        batch.projectionMatrix = HUDCamera.combined
        HUDCamera.update()
        HUDStage.draw()
        HUDStage.act()
    }


    fun drawOverlays() {
        gameCamera.update()
        shapeRenderer.projectionMatrix = gameCamera.combined
        val moveBorders = EntitySelectionSystem.movementBorders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        if (moveBorders.isNotEmpty()) {
            moveBorders.forEachIndexed { index, border ->
                shapeRenderer.color = EntitySelectionSystem.movementColors(moveBorders.size - index - 1)
                border.forEach { line ->
                    shapeRenderer.line(line.startX, line.startY, line.endX, line.endY)
                }
            }
        }
        val selectLines = EntitySelectionSystem.selectionBorders
        if (selectLines.isNotEmpty()) {
            shapeRenderer.color = EntitySelectionSystem.selectColors()
            selectLines.forEach { line ->
                shapeRenderer.line(line.startX, line.startY, line.endX, line.endY)
            }
        }
        shapeRenderer.end()

        // TODO: Move the drawing of entity mini health bars into this system
    }


    fun newMenu() {
        menuStage.clear()
    }


    fun newHUD() {
        HUDStage.clear()
    }


    fun pan(deltaX: Float, deltaY: Float) {
        gameCamera.translate(gameCamera.zoom * -(deltaX), gameCamera.zoom * (deltaY))
    }


    fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, false)
        menuViewport.update(width, height, true)
        HUDViewport.update(width, height, true)
    }


    fun renderStage() {
        Gdx.gl.glClearColor(.0F, .168F, .212F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        menuStage.act()
        menuStage.draw()
    }


    fun setScrollFocus(actor: Actor?) {
        menuStage.scrollFocus = actor
    }


    fun switchToStage() {
        menuStage.clear()
        menuCamera.update()
    }


    fun switchToGame() {
        menuStage.clear()
        gameViewport.apply()
        gameCamera.update()
    }


    fun updateGameCamera() {
        batch.projectionMatrix = gameCamera.combined
        gameViewport.apply()
        gameCamera.update()
    }


    fun unfocus() {
        HUDStage.unfocusAll()
        menuStage.unfocusAll()
    }


    fun unproject(x: Int, y: Int): Vector2 {
        val vector = gameCamera.unproject(Vector3(x.toFloat(), y.toFloat(), 0F))
        return Vector2(vector.x, vector.y)
    }


    fun zoom(amount: Int) {
        gameCamera.zoom += 0.1F * amount.toFloat()
        if (gameCamera.zoom < 0.2) {
            gameCamera.zoom = 0.2F
        }
    }

}