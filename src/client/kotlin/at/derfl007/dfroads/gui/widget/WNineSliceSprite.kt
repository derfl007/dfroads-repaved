package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WSprite
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.texture.Scaling
import net.minecraft.util.Identifier
import kotlin.math.roundToInt

class WNineSliceSprite(val image: Identifier, val scaling: Scaling) : WSprite(image) {

    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (scaling.type == Scaling.Type.NINE_SLICE && scaling is Scaling.NineSlice) {
            val borderTop = scaling.border.top / scaling.height.toFloat()
            val borderLeft = scaling.border.left / scaling.width.toFloat()
            val borderBottom = scaling.border.bottom / scaling.height.toFloat()
            val borderRight = scaling.border.right / scaling.width.toFloat()

            val borderLeftRelative = (borderLeft * 90f).roundToInt()
            val borderTopRelative = (borderTop * 90f).roundToInt()
            val borderRightRelative = (borderRight * 90f).roundToInt()
            val borderBottomRelative = (borderBottom * 90f).roundToInt()

            // corners
            ScreenDrawing.texturedRect(
                context,
                x,
                y,
                borderLeftRelative,
                borderTopRelative,
                image,
                0f,
                0f,
                borderLeft,
                borderTop,
                tint
            ) //top left
            ScreenDrawing.texturedRect(
                context,
                x,
                height + y - borderBottomRelative,
                borderLeftRelative,
                borderBottomRelative,
                image,
                0f,
                1f - borderBottom,
                borderLeft,
                1f,
                tint
            ) // bottom left
            ScreenDrawing.texturedRect(
                context,
                x + width - borderRightRelative,
                height + y - borderBottomRelative,
                borderRightRelative,
                borderBottomRelative,
                image,
                1f - borderRight,
                1f - borderBottom,
                1f,
                1f,
                tint
            ) // bottom right
            ScreenDrawing.texturedRect(
                context,
                x + width - borderRightRelative,
                y,
                borderRightRelative,
                borderTopRelative,
                image,
                1f - borderRight,
                0f,
                1f,
                borderTop,
                tint
            ) // top right

            // edges
            ScreenDrawing.texturedRect(
                context,
                x,
                y + borderTopRelative,
                 borderLeftRelative,
                 height - (borderTopRelative + borderBottomRelative),
                image,
                0f,
                borderTop,
                borderLeft,
                1f - borderBottom,
                tint
            ) // left edge
            ScreenDrawing.texturedRect(
                context,
                x + borderLeftRelative,
                y,
                width - (borderLeftRelative + borderRightRelative),
                borderTopRelative,
                image,
                borderLeft,
                0f,
                1 - borderRight,
                borderTop,
                tint
            ) // top line
            ScreenDrawing.texturedRect(
                context,
                x + width - borderRightRelative,
                y + borderTopRelative,
                borderRightRelative,
                height - (borderTopRelative + borderBottomRelative),
                image,
                1 - borderRight,
                borderTop,
                1f,
                1f - borderBottom,
                tint
            ) // right edge
            ScreenDrawing.texturedRect(
                context,
                x + borderLeftRelative,
                height + y - borderBottomRelative,
                width - (borderLeftRelative + borderRightRelative),
                borderBottomRelative,
                image,
                borderBottom,
                1 - borderBottom,
                1f - borderBottom,
                1f,
                tint
            ) // bottom line

            // center
            ScreenDrawing.texturedRect(
                context,
                x + borderLeftRelative,
                y + borderBottomRelative,
                width - (borderLeftRelative + borderRightRelative),
                height - (borderBottomRelative + borderTopRelative),
                image,
                borderLeft,
                borderTop,
                1f - borderRight,
                1f - borderBottom,
                tint
            )

        }
    }
}