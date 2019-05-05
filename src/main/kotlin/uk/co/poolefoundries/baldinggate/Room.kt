package uk.co.poolefoundries.baldinggate

import com.badlogic.ashley.core.Entity

fun roomOf(width: Int, height: Int) : List<Entity> {
    val entities = mutableListOf<Entity>()

    for (i in 0 until width) {
        for (j in 0 until height) {
            val entity = if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
                wallTileAt(i, j)
            } else {
                floorTileAt(i, j)
            }
            entities.add(entity)
        }
    }

    entities.add(createSkeleton(1, 1))

    return entities
}

fun floorTileAt(x: Int, y: Int) : Entity {
    val entity = Entity()

    entity.add(VisualComponent(TextureRenderable(Resources.floor())))
    entity.add(PositionComponent(x, y))

    return entity
}

fun wallTileAt(x: Int, y: Int) : Entity {
    val entity = Entity()

    entity.add(VisualComponent(TextureRenderable(Resources.wall())))
    entity.add(PositionComponent(x, y))
    entity.add(WallComponent)

    return entity
}