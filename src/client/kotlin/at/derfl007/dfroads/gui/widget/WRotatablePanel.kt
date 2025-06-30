package at.derfl007.dfroads.gui.widget

import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.RotationAxis

/**
 * Panel which can rotate all child elements around their center
 */
class WRotatablePanel(var rotation: Float = 0f) : WPlainPanel() {

    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (backgroundPainter != null) backgroundPainter.paintBackground(context, x, y, this)

        for (child in children) {
            val matrices = context!!.matrices
            matrices.push()
            matrices.translate(x.toFloat(), y.toFloat(), 0f)
            matrices.translate((child.x + (child.width / 2)).toDouble(), (child.y + (child.height / 2)).toDouble(), 0.0)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation))
            child.paint(context, -child.width / 2, -child.height / 2, mouseX - child.getX(), mouseY - child.getY())
            matrices.pop()
        }
    }

    fun rotate(degrees: Float) {
        this.rotation = degrees
    }
}