package uk.co.poolefoundries.baldinggate

import com.badlogic.gdx.graphics.Texture

object Resources {
    val tileWidth = 25

    private val resources = mutableMapOf<String, Texture>()

    fun get(name: String): Texture {
        return if (resources.containsKey(name)) {
            resources[name]!!
        } else {
            val texture = Texture(name)
            resources[name] = texture
            texture
        }
    }

    fun wall() = get("wall.png")
    fun floor() = get("floor.png")
    fun skeleton() = get("skeleton.png")
}