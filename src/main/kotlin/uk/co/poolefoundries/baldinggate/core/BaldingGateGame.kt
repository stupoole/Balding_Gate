package uk.co.poolefoundries.baldinggate.core

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import uk.co.poolefoundries.baldinggate.screens.LevelEditScreen
import uk.co.poolefoundries.baldinggate.screens.MainMenuScreen
import uk.co.poolefoundries.baldinggate.systems.CameraSystem


object BaldingGateGame : Game() {

    var engine = Engine()

    override fun create() {
        engine.addSystem(CameraSystem)
        setScreen(MainMenuScreen(this))
    }


    override fun resize(width: Int, height: Int) {
        engine.getSystem(CameraSystem::class.java).resize(width, height)
    }

    override fun dispose() {
        engine.getSystem(CameraSystem::class.java).dispose()
    }

}

