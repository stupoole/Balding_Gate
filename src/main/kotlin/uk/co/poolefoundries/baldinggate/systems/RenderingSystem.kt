package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Array
import uk.co.poolefoundries.baldinggate.core.*

object RenderingSystem : EntitySystem() {
    private const val tileSize = 25f // TODO somehow make everything that needs this value get it from the same place
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val visualComponentMapper = ComponentMapper.getFor(VisualComponent::class.java)
    private val colorComponentMapper = ComponentMapper.getFor(ColorComponent::class.java)
    private val playerFamily: Family = Family.all(PlayerComponent::class.java, PositionComponent::class.java, VisualComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java, PositionComponent::class.java, VisualComponent::class.java).get()
    private val floorsFamily: Family = Family.all(FloorComponent::class.java, PositionComponent::class.java, VisualComponent::class.java).get()
    private val wallsFamily: Family = Family.all(WallComponent::class.java, PositionComponent::class.java, VisualComponent::class.java).get()
    private fun mobs() = engine.getEntitiesFor(playerFamily).toList() + engine.getEntitiesFor(enemyFamily).toList()
    private fun tiles() = engine.getEntitiesFor(floorsFamily).toList() + engine.getEntitiesFor(wallsFamily).toList()


    override fun addedToEngine(engine: Engine) {

    }

    override fun update(deltaTime: Float) {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        cameraSystem.updateGameCamera()
        Gdx.gl.glClearColor(.0F, .168F, .212F, 1F)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        cameraSystem.batch.begin()
        tiles().forEach { drawEntity(it, deltaTime) }
        cameraSystem.batch.end()

        cameraSystem.batch.begin()
        mobs().forEach { drawEntity(it, deltaTime) }
        cameraSystem.batch.end()

        cameraSystem.drawHUD()
        cameraSystem.drawOverlays()
    }

    private fun drawEntity(entity: Entity, delta: Float) {
        val cameraSystem = engine.getSystem(CameraSystem::class.java)
        val visComp = visualComponentMapper.get(entity)
        val pos = visComp.updateAndGetAnimationPos(delta) ?: positionMapper.get(entity)
        cameraSystem.batch.color = colorComponentMapper.get(entity).color
        visComp.renderable.draw(cameraSystem.batch, pos.x * tileSize, pos.y * tileSize)

    }
}