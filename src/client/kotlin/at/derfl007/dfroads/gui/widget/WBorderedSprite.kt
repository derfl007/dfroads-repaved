package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.Texture
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

open class WBorderedSprite: WSprite {

    constructor(image: Identifier, borderColor: Int = -1): super(image) {
        this.borderColor = borderColor
    }

    constructor(texture: Texture, borderColor: Int = -1) : super(texture) {
        this.borderColor = borderColor
    }

    var borderColor: Int = -1

    override fun paint(
        context: DrawContext?,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int
    ) {
        if (borderColor != -1) {
            context?.drawBorder(x, y, width, height, borderColor)
        }
        super.paint(context, x, y, mouseX, mouseY)
    }
}