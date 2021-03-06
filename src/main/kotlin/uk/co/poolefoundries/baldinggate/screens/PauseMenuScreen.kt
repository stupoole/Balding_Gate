package uk.co.poolefoundries.baldinggate.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import uk.co.poolefoundries.baldinggate.core.BaldingGateGame
import uk.co.poolefoundries.baldinggate.desktop.DesktopLauncher
import uk.co.poolefoundries.baldinggate.systems.CameraSystem
import uk.co.poolefoundries.baldinggate.systems.editor.LevelEditorSystem

// will need to take in previous screen and just display this before returning to the previous screen on resume or
// change to options or quit
// options screen should do the same thing so that you can go back from options to pause and then back to game
class PauseMenuScreen(val game: BaldingGateGame, val previousScreen: ScreenAdapter) : ScreenAdapter() {

    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"), atlas)
    private val defaultAtlas = TextureAtlas(Gdx.files.internal("UISkins/default/skin/uiskin.atlas"))
    private val defaultSkin = Skin(Gdx.files.internal("UISkins/default/skin/uiskin.json"),defaultAtlas)

    private val table = Table()
    private val scrollTable = Table()
    private val scrollPane = ScrollPane(scrollTable, skin)

    init {

        table.center().center()
        table.setFillParent(true)
        scrollTable.setFillParent(false)
        scrollPane.fadeScrollBars = false

        val resumeButton = TextButton("resume", skin, "embossed")
        val levelsButton = TextButton("levels", skin)
        val editLevelsButton = TextButton("Edit Level", skin)
        val optionsButton = TextButton("options", skin)
        val quitButton = TextButton("quit", skin)

        resumeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = previousScreen
            }
        })
        levelsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelSelectScreen(game, this@PauseMenuScreen, previousScreen)
            }
        })
        editLevelsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = LevelEditSelectScreen(game, this@PauseMenuScreen, previousScreen)
            }
        })
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = OptionsScreen(game, this@PauseMenuScreen)
            }
        })
        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                DesktopLauncher.application.exit()
            }
        })

        scrollTable.add(resumeButton).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)
        scrollTable.row()
        scrollTable.add(levelsButton).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)
        scrollTable.row()
        scrollTable.add(editLevelsButton).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)
        scrollTable.row()
        if (previousScreen is LevelEditScreen){
            val levelNameField = TextField(previousScreen.levelName, defaultSkin)
            val saveLevelButton = TextButton("Save Level", skin)
            saveLevelButton.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (LevelEditorSystem.saveLevel(levelNameField.text)){
                        levelNameField.text = "Saved Successfully"
                    } else {
                        levelNameField.text = "Save Failed?"
                    }
                }
            })
            scrollTable.add(levelNameField).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)
            scrollTable.row()
            scrollTable.add(saveLevelButton).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)
            scrollTable.row()
        }
        scrollTable.add(optionsButton).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)
        scrollTable.row()
        scrollTable.add(quitButton).padBottom(4F).expand().fill().maxHeight(100F).maxWidth(450F)

        table.add(scrollPane).fill().expand()
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun show() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.switchToStage()
        cameraSystem.addActorToStage(table)
        cameraSystem.setScrollFocus(scrollPane)
        Gdx.input.inputProcessor = cameraSystem.menuStage
    }

    override fun render(delta: Float) {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.renderStage()
    }

    override fun dispose() {
        val cameraSystem = game.engine.getSystem(CameraSystem::class.java)
        cameraSystem.newMenu()
        skin.dispose()
        atlas.dispose()
    }

}
