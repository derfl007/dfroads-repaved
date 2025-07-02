package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WWidget
import net.minecraft.client.gui.DrawContext

class WDragArea(var maxX: Int = 1, var maxY: Int = 1): WPlainPanel() {

    companion object {
        const val PADDING = 4
    }

    override fun add(w: WWidget?, x: Int, y: Int, width: Int, height: Int) {
        addWidget(w, x, y, width, height)
        super.add(w, x, y, width, height)
    }

    override fun canResize() = true

    private fun addWidget(w: WWidget?, x: Int, y: Int, width: Int, height: Int) {
        if (w is WDraggableWidget) {
            w.selectHandlers += {
                children.forEach {
                    if (it is WDraggableWidget && it != w) {
                        it.isSelected = false
                    }
                }
            }
            w.setSize(width + 2 * PADDING, height + 2 * PADDING)
            w.setLocation(x - PADDING, y - PADDING)
        }
    }

    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        super.paint(context, x, y, mouseX, mouseY)
    }
}