package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.core.ColorComponent
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.core.VisualComponent

object RenderingSystem : EntitySystem() {
    private const val tileSize = 25f // TODO somehow make everything that needs this value get it from the same place
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val visualComponentMapper = ComponentMapper.getFor(VisualComponent::class.java)
    private val colorComponentMapper = ComponentMapper.getFor(ColorComponent::class.java)
    private var entities = ImmutableArray(Array<Entity>())


    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent::class.java, VisualComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        cameraSystem.updateGameCamera()
        Gdx.gl.glClearColor(.1F, .12F, .16F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        cameraSystem.batch.begin()
        cameraSystem.batch.color = Color.WHITE
        entities.forEach { drawEntity(it, deltaTime) }
        cameraSystem.batch.end()
    }

    private fun drawEntity(entity: Entity, delta: Float) {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val visComp = visualComponentMapper.get(entity)
        val pos = visComp.updateAndGetAnimationPos(delta) ?: positionMapper.get(entity)
        cameraSystem.batch.color = colorComponentMapper.get(entity).color
        visComp.renderable.draw(cameraSystem.batch, pos.x * tileSize, pos.y * tileSize)

    }
}