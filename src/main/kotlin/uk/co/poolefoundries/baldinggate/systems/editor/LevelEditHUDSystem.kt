package uk.co.poolefoundries.baldinggate.systems.editor

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import uk.co.poolefoundries.baldinggate.systems.CameraSystem

object LevelEditHUDSystem : EntitySystem() {
    private val atlas = TextureAtlas(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.atlas"))
    private val skin = Skin(Gdx.files.internal("UISkins/StoneButtons/main-menu-buttons.json"),atlas )
    private val defaultAtlas = TextureAtlas(Gdx.files.internal("UISkins/default/skin/uiskin.atlas"))
    private val defaultSkin = Skin(Gdx.files.internal("UISkins/default/skin/uiskin.json"),defaultAtlas)

    private val table = Table()
    private val scrollTable = Table()
    private val scrollPane = ScrollPane(
        scrollTable,
        skin
    )
    var selectedTile:FileHandle? = null
    var selectedDrawable:TextureRegionDrawable? = null
    var selectedCell:Cell<Image>? = null
    var selectedLabel: Cell<Label>? = null

    init {
        table.right().center()
//        scrollTable.debug()
//        table.debug()
        table.setFillParent(true)
        scrollTable.setFillParent(false)
        scrollPane.fadeScrollBars = false

        val dirHandles = listOf(
            Gdx.files.internal("floors/"),
            Gdx.files.internal("walls/"),
            Gdx.files.internal("interactables/"),
            Gdx.files.internal("mobs/"),
            Gdx.files.internal("characters/")
        )

        // filter out all tiles that do not have a json with same name
        dirHandles.forEach { dirHandle ->
            dirHandle.list().filter { it.extension() == "png" }.forEach { tile ->
                val texture = Texture(tile)
                val name = tile.nameWithoutExtension()
                val button = ImageButton(TextureRegionDrawable(texture))
                button.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        selectedDrawable = TextureRegionDrawable(Texture(tile))
                        selectedCell?.setActor(Image(selectedDrawable))
                        selectedLabel?.setActor(Label(name, defaultSkin))
                        selectedTile = tile
                        // If going from pause menu to level select to level this does not work.
                    }
                })
                scrollTable.add(button).fill().maxHeight(25F).spaceTop(5F)
                scrollTable.row()
                scrollTable.add(
                    Label(
                        name.substring(0, minOf(10, name.length)),
                        defaultSkin
                    )
                ).spaceBottom(5F)

                scrollTable.row()
            }
        }
        scrollTable.add().expand()// fill bottom section of scrollpane with empty space so that stuff is at top


        val selectedTable = Table()
        selectedTable.add(Label("Selected:", defaultSkin)).center()
        selectedTable.row()
        selectedCell = selectedTable.add(Image(selectedDrawable)).width(75F).height(75F).top()
        selectedTable.row()
        selectedLabel = selectedTable.add(Label("", defaultSkin)).top()
        selectedTable.row()
        table.add(selectedTable).top()
        table.add().expand().fill()
        table.add(scrollPane).fill().width(120F)
        table.row()

        val backgroundTexture = atlas.createPatch("background")
        scrollTable.background = NinePatchDrawable(backgroundTexture)

    }

    fun show() {
        CameraSystem.addActorToHUD(table)
    }

    override fun addedToEngine(engine: Engine?) {
        CameraSystem.addActorToHUD(table)
    }

    fun hide() {
        CameraSystem.newHUD()
    }

    override fun update(deltaTime: Float) {
        CameraSystem.drawOrigin()
    }

}