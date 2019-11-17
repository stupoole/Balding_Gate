package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.Component

data class PositionComponent(val x: Int, val y: Int) : Component
data class VisualComponent(val renderable: Renderable) : Component
object WallComponent : Component