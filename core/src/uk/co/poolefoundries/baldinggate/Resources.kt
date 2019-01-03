package uk.co.poolefoundries.baldinggate

import com.badlogic.gdx.graphics.Texture

object Resources {
    val tileWidth = 25

    private val resources = mutableMapOf<String, Texture>()

    private fun getResource(name: String): Texture {
        return if (resources.containsKey(name)) {
            resources[name]!!
        } else {
            val texture = Texture(name)
            resources[name] = texture
            texture
        }
    }

    fun wall() = getResource("wall.png")
    fun floor() = getResource("floor.png")
    fun skeleton() = getResource("skeleton.png")
}