package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WWidget
import net.minecraft.client.gui.DrawContext

/**
 * Allows applying scaling to all child widgets
 * note that all children will have pos 0, 0 and the same size as their parent
 */
class WScalableWidget(var scale: Float = 1f) : WPlainPanel() {

    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (backgroundPainter != null) backgroundPainter.paintBackground(context, x, y, this)
        for (child in children) {

            val matrices = context!!.matrices
            matrices.pushMatrix()
            matrices.translate(x.toFloat(), y.toFloat())
            matrices.scale(scale, scale)
            child.paint(context, 0, 0, mouseX, mouseY)
            matrices.popMatrix()
        }
    }

    override fun setSize(x: Int, y: Int) {
        super.setSize(x, y)
        children.forEach {
            it.setSize(x, y)
        }
    }

    override fun setLocation(x: Int, y: Int) {
        super.setLocation(x, y)
        children.forEach {
            it.setLocation(0, 0) // always set children to 0,0
        }
    }

    fun add(child: WWidget) {
        add(child, this.x, this.y, this.width, this.height)
    }
}