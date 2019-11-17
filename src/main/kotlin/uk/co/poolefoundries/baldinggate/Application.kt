package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ScreenViewport
import uk.co.poolefoundries.baldinggate.model.loadLevel
import uk.co.poolefoundries.baldinggate.model.loadLevelJson
import uk.co.poolefoundries.baldinggate.model.toEntities


// TODO: Add a turn based entity system in order to make all updates to game at the end of a turn

// Main game application
class Application : ApplicationAdapter() {


    override fun create() {
        Game.init()
    }

    override fun resize(width: Int, height: Int) {
        Game.viewport.update(width, height)
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        Game.engine.update(delta)
    }
}

object Game {
    val engine = Engine()
    private val camera = OrthographicCamera()
    val viewport = ScreenViewport(camera)

    fun init() {
        loadLevel("level").toEntities().forEach(engine::addEntity)
        engine.addSystem(RenderingSystem(camera))
        engine.addSystem(SkeletonSystem)
        engine.addSystem(PlayerSystem)

        val input = InputMultiplexer()
        input.addProcessor(PanHandler(camera))
        input.addProcessor(PlayerInputHandler)
        Gdx.input.inputProcessor = input
    }

}

interface Renderable {
    fun draw(batch: Batch, x: Float, y: Float)
}

data class TextureRenderable(val texture: Texture) : Renderable {
    override fun draw(batch: Batch, x: Float, y: Float) {
        batch.draw(texture, x, y)
    }
}


class RenderingSystem(private val camera: OrthographicCamera) : EntitySystem() {
    private val tileSize = 25f

    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val visualComponentMapper = ComponentMapper.getFor(VisualComponent::class.java)

    private var entities = ImmutableArray(com.badlogic.gdx.utils.Array<Entity>())

    private val batch = SpriteBatch()

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent::class.java, VisualComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        camera.update()
        batch.projectionMatrix = camera.combined

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        entities.forEach(::drawEntity)
        batch.end()
    }

    private fun drawEntity(entity: Entity) {
        val pos = positionMapper.get(entity)
        val visualComponent = visualComponentMapper.get(entity)

        visualComponent.renderable.draw(batch, pos.x * tileSize, pos.y * tileSize)
    }
}