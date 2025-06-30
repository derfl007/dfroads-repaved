package at.derfl007.dfroads.texture

import net.minecraft.client.texture.Sprite

/**
 * TOP-LEFT IS U=V=0!!!
 */
object SpriteExtension {
    fun Sprite.calculateU(ratio: Float, offset: Float = 0f): Float {
        return (maxU - minU) * ratio + minU + offset
    }

    fun Sprite.calculateV(ratio: Float, offset: Float = 0f): Float {
        return (maxV - minV) * ratio + minV + offset
    }
}