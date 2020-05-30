package uk.co.poolefoundries.baldinggate.screens


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.core.TextureRenderable

class MainMenuScreen(val game: BaldingGateGame) : ScreenAdapter() {

    var stage = Stage(game.viewport, game.batch)
    val atlas = TextureAtlas(files.internal("UISkins/default/skin/uiskin.atlas"))
    val skin = Skin(files.internal("UISkins/default/skin/uiskin.json"), atlas)

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        Gdx.input.inputProcessor = stage

        val table = Table()
        table.setFillParent(true)
        table.center()
        val playButton = ImageButton(TextureRegionDrawable(TextureRegion(Texture(files.internal("MainMenuButtons_0.png")))))
        val optionsButton = ImageButton(TextureRegionDrawable(TextureRegion(Texture(files.internal("MainMenuButtons_1.png")))))
        val quitButton = ImageButton(TextureRegionDrawable(TextureRegion(Texture(files.internal("MainMenuButtons_2.png")))))

        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelScreen(game)
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // Options screen? maybe dynamic menus?
            }
        })

        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //todo figure out how to quit
            }
        })

        table.add(playButton)
        table.row()
        table.add(optionsButton)
        table.row()
        table.add(quitButton)
        stage.addActor(table)
    }


    override fun render(delta: Float) {
//        game.batch.begin()
//        game.batch.end()
        //todo: try with and without batch begin/end
        stage.draw()
    }

    override fun dispose() {
        //todo dispose of the stage for sure. probabyl skin and atlas too
//        stage.dispose()
        skin.dispose()
        atlas.dispose()
    }


}