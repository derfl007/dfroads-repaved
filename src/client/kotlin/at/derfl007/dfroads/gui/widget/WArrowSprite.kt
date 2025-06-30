package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WSprite
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

class WArrowSprite: WSprite {

    constructor(image: Identifier): super(image) {}

    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        val texture = frames[0] // we only support single frames in this class

        ScreenDrawing.texturedRect(
            context,
            x,
            y,
            width,
            width,
            texture.image,
            texture.u1,
            texture.v1,
            texture.u2,
            texture.v1 + 0.9f * texture.v2,
            tint,
            1f
        )

        ScreenDrawing.texturedRect(
            context,
            x,
            y + width,
            width,
            height - width,
            texture.image,
            texture.u1,
            texture.v1 + 0.9f * texture.v2,
            texture.u2,
            texture.v2,
            tint,
            1f
        )
    }
}