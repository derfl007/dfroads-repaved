package at.derfl007.dfroads.gui.widget

import at.derfl007.dfroads.util.toRad
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import net.minecraft.client.gui.DrawContext

/**
 * Panel which can rotate all child elements around their center
 */
class WRotatablePanel(var rotation: Float = 0f) : WPlainPanel() {

    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (backgroundPainter != null) backgroundPainter.paintBackground(context, x, y, this)

        for (child in children) {
            val matrices = context!!.matrices
            matrices.pushMatrix()
            matrices.translate(x.toFloat(), y.toFloat())
            matrices.translate(child.x + (child.width / 2f), child.y + (child.height / 2f))
            matrices.rotate(rotation.toRad())
            child.paint(context, -child.width / 2, -child.height / 2, mouseX - child.getX(), mouseY - child.getY())
            matrices.popMatrix()
        }
    }

    fun rotate(degrees: Float) {
        this.rotation = degrees
    }
}