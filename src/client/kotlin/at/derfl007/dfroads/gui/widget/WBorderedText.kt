package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class WBorderedText(text: Text, var borderColor: Int = -1): WText(text) {
    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        context?.drawBorder(x, y, width, height, if (borderColor != -1) borderColor else 0xff000000.toInt())
        super.paint(context, x, y, mouseX, mouseY)
    }
}