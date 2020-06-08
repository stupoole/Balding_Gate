package uk.co.poolefoundries.baldinggate.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import uk.co.poolefoundries.baldinggate.core.EnemyComponent
import uk.co.poolefoundries.baldinggate.core.PlayerComponent
import uk.co.poolefoundries.baldinggate.core.PositionComponent
import uk.co.poolefoundries.baldinggate.core.StatsComponent
import uk.co.poolefoundries.baldinggate.statusbars.HealthBar
import uk.co.poolefoundries.baldinggate.statusbars.StaminaBar

object HUDSystem : EntitySystem() {
    private val playerFamily: Family = Family.all(PlayerComponent::class.java).get()
    private val enemyFamily: Family = Family.all(EnemyComponent::class.java).get()
    private fun mobs() = engine.getEntitiesFor(playerFamily).toList() + engine.getEntitiesFor(enemyFamily).toList()
    private fun (Entity).toStats() = getComponent(StatsComponent::class.java).stats
    private fun (Entity).toPosition() = getComponent(PositionComponent::class.java)
    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StatusBars/status-bars.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StatusBars/status-bars.json"), atlas)
    private val table = Table()
    private val miniHealthBarStyle = skin.get("mini", HealthBar.HealthBarStyle::class.java)

    private val healthBar = HealthBar(1F, 0F, skin.get("default", HealthBar.HealthBarStyle::class.java))
    private val staminaBar = StaminaBar(1, 0, skin.get("default", StaminaBar.StaminaBarStyle::class.java))
    private val miniHealthBars = mutableMapOf<Entity, HealthBar>()
    private const val animationDuration = 0.1F

    init {
        table.bottom().left()
        table.setFillParent(true)
        healthBar.animateDuration = animationDuration

        table.row().expandY()
        table.row().uniformX()
        table.add(healthBar).fill().expandX().padBottom(10F).maxWidth(500F).left()
        table.add().expandX()
        table.add().expandX()
        table.row().uniformX()
        table.add(staminaBar).fill().expandX().padBottom(10F).maxWidth(500F).left()
        table.add().expandX()
        table.add().expandX()

    }

    override fun update(deltaTime: Float) {
        val stats = EntitySelectionSystem.getSelectedPlayerStats()
        if (stats == null) {
            staminaBar.updateValues(0, 0)
            healthBar.value = 0F
        } else {
            healthBar.value = stats.hitPoints.toFloat() / stats.vitality.toFloat()
            staminaBar.updateValues(stats.currentAP, stats.maxAP)
        }
        val batch  = CameraSystem.batch
        batch.begin()
        CameraSystem.batch.projectionMatrix = CameraSystem.gameCamera.combined
        miniHealthBars.forEach { (mob, bar) ->
            if( mob.toStats().hitPoints >0) {
                bar.value = (mob.toStats().hitPoints.toFloat() / mob.toStats().vitality.toFloat())
                bar.drawAt(batch, mob.toPosition().x * 25F, mob.toPosition().y * 25F, 25F, 0F)
            }
        }
        batch.end()
    }

    fun show() {
        for (mob in mobs()) {
            miniHealthBars[mob] = HealthBar(1F, 1F, miniHealthBarStyle)
        }
        CameraSystem.addActorToHUD(table)
    }

    fun hide() {
        CameraSystem.newHUD()
    }

    override fun addedToEngine(engine: Engine?) {
        CameraSystem.addActorToHUD(table)
    }

}