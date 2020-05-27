package uk.co.poolefoundries.baldinggate.screens


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import uk.co.poolefoundries.baldinggate.desktop.DesktopLauncher

object MainMenuScreen : Screen {

    val spriteBatch = SpriteBatch()
    val atlas = TextureAtlas(files.internal("UISkins/default/skin/uiskin.atlas"))
    private val camera = OrthographicCamera()
    private val viewport = ScreenViewport(camera)
    val stage = Stage(viewport, spriteBatch)
    val skin = Skin(files.internal("UISkins/default/skin/uiskin.json"), atlas)


    override fun hide() {

    }

    override fun show() {

        camera.position.set(Vector2(viewport.worldWidth / 2, viewport.worldHeight / 2), 0.0F)
        viewport.apply()
        camera.update()
        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        table.top()

        val playButton = TextButton("Play", skin)
//        val optionsButton = TextButton("Options", skin)
        val quitButton = TextButton("Quit", skin)

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // TODO make this initiate the game screen
                DesktopLauncher.appAdapter.showLevelScreen()
            }
        })

        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                DesktopLauncher.quit()
            }
        })

        table.add(playButton)
        table.row()
        table.add(quitButton)

        stage.addActor(table)
    }


    override fun render(delta: Float) {

        camera.update()
        Gdx.gl.glClearColor(.1F, .12F, .16F, 1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act()
        stage.draw()

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {

        this.viewport.update(width, height)

    }

    override fun dispose() {
        skin.dispose()
        atlas.dispose()
        
    }

}