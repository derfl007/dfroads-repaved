package at.derfl007.dfroads.gui.widget

import at.derfl007.dfroads.gui.widget.WDragArea.Companion.PADDING
import at.derfl007.dfroads.renderer.util.RendererUtil.Companion.calculateScalingFactor
import io.github.cottonmc.cotton.gui.widget.WWidget
import io.github.cottonmc.cotton.gui.widget.data.InputResult
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.texture.Scaling
import net.minecraft.util.math.RotationAxis
import kotlin.math.*

open class WDraggableWidget(
    val widget: WWidget,
    val canMove: Boolean = true,
    val canResize: Boolean = true,
    val canRotate: Boolean = true,
    val canSelect: Boolean = true,
    var outlineColor: Int = -1,
    var rotation: Int = 0, // in degrees
    val canIgnoreBoundaries: Boolean = false
) : WWidget() {

    var isSelected = false
    var isDragging = false
    var isRotating = false
    var isResizingX = false
    var isResizingY = false
    val isResizing
        get() = isResizingY || isResizingX
    var offsetX = 0
    var offsetY = 0
    var snapDistance = 0

    // values before snapping is applied
    var realWidth = width
    var realHeight = height
    var realX = x
    var realY = y

    // handlers
    var selectHandlers: List<(() -> Unit)> = mutableListOf()
    var resizeHandlers: List<((width: Int, height: Int) -> Unit)> = mutableListOf()
    var rotationHandlers: List<((degrees: Int) -> Unit)> = mutableListOf()
    var moveHandlers: List<((x: Int, y: Int) -> Unit)> = mutableListOf()

    init {
        this.widget.setSize(width - 2 * PADDING, height - 2 * PADDING)
        this.widget.setLocation(PADDING, PADDING)
        realX = this.x
        realY = this.y
        realWidth = this.width
        realHeight = this.height
    }

    override fun onMouseDown(x: Int, y: Int, button: Int): InputResult? {
        if (!isSelected) return InputResult.IGNORED
        if (isAtRightEdge(x, y) || isAtBottomEdge(x, y)) { // right edge
            isResizingX = isAtRightEdge(x, y)
            isResizingY = isAtBottomEdge(x, y)
        } else if (isAtTopEdge(x, y)) {
            isRotating = canRotate
        } else {
            isDragging = canMove
        }
        offsetX = x
        offsetY = y
        return InputResult.PROCESSED
    }

    override fun onMouseUp(x: Int, y: Int, button: Int): InputResult? {
        if (!isResizing && !isDragging && !isRotating) {
            isSelected = canSelect
            if (canSelect) selectHandlers.forEach { it() }
        } else {
            if (isDragging) {
                moveHandlers.forEach { it(this.x + PADDING, this.y + PADDING) }
                isDragging = false
                realX = this.x
                realY = this.y
            }
            if (isResizing) {
                resizeHandlers.forEach { it(this.width - 2 * PADDING, this.height - 2 * PADDING) }
                isResizingX = false
                isResizingY = false
                realWidth = this.width
                realHeight = this.height
            }
            if (isRotating) {
                rotationHandlers.forEach { it(this.rotation) }
                isRotating = false
            }
        }
        return InputResult.PROCESSED
    }

    override fun onMouseDrag(x: Int, y: Int, button: Int, deltaX: Double, deltaY: Double): InputResult? {
        if (!isSelected) return InputResult.IGNORED
        if (isRotating) {
            val degrees = Math.toDegrees(atan2(x.toDouble() - offsetX, this.width / 2.0))
            this.rotation = 45 * (degrees / 45f).roundToInt()
            println("atan2(${x.toDouble() - offsetX}, ${this.width / 2.0}) = ${this.rotation}")
        }
        if (isDragging) {
            this.x += x - offsetX
            this.y += y - offsetY
            realX += this.x
            realY += this.y
        }
        if (isResizingX && isResizingY) {
            val averageDelta = ((deltaX + deltaY) / 2).toInt()
            this.width += averageDelta
            this.height += averageDelta
        } else {
            if (isResizingX) {
                this.width += deltaX.toInt()
                realWidth += deltaX.toInt()
            }
            if (isResizingY) {
                this.height += deltaY.toInt()
                realHeight += deltaY.toInt()
            }
        }
        if (snapDistance != 0) {
            this.x = snapDistance * round(realX / snapDistance.toFloat()).toInt()
            this.y = snapDistance * round(realY / snapDistance.toFloat()).toInt()
            this.width = snapDistance * round(realWidth / snapDistance.toFloat()).toInt()
            this.height = snapDistance * round(realHeight / snapDistance.toFloat()).toInt()
        }
        this.rotation = 45 * round(this.rotation / 45f).toInt()
        val minX = if (canIgnoreBoundaries) 0 else PADDING
        val minY = if (canIgnoreBoundaries) 0 else PADDING
        val maxX = if (canIgnoreBoundaries) parent!!.width else (parent as WDragArea).maxX
        val maxY = if (canIgnoreBoundaries) parent!!.height else (parent as WDragArea).maxY
        try {
            this.width = this.width.coerceIn(10, maxX - PADDING)
            this.height = this.height.coerceIn(10, maxY - PADDING)
            this.x = this.x.coerceIn(minX, maxX - this.width)
            this.y = this.y.coerceIn(minY, maxY - this.height)
        } catch (e: Exception) {
            println(e)
        }
        if (this.widget is WNineSliceSprite) {
            val scaling = this.widget.scaling
            if (scaling.type == Scaling.Type.NINE_SLICE && scaling is Scaling.NineSlice) {
                val borderTop = scaling.border.top / scaling.height.toFloat()
                val borderLeft = scaling.border.left / scaling.width.toFloat()
                val borderBottom = scaling.border.bottom / scaling.height.toFloat()
                val borderRight = scaling.border.right / scaling.width.toFloat()

                val horizontalBorders = borderLeft * 90 + borderRight * 90 + PADDING * 2
                val verticalBorders = borderTop * 90 + borderBottom * 90 + PADDING * 2

                this.width = max(this.width, horizontalBorders.toInt())
                this.height = max(this.height, verticalBorders.toInt())
            }
        }
        this.widget.setSize(this.width - PADDING * 2, this.height - PADDING * 2)
        if (this.widget is WScalableWidget) {
            this.widget.scale = (1 / 9f) * ((this.height - PADDING * 2) * 0.9f)
        }
        return InputResult.PROCESSED
    }

    override fun paint(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        widget.setSize(this.width - 8, this.height - 8)
        val matrices = context.matrices
        matrices.push()
        matrices.translate(PADDING.toFloat(), PADDING.toFloat(), 0f)
        matrices.translate(x.toFloat(), y.toFloat(), 0f)
        matrices.translate(((widget.width / 2f)), ((widget.height / 2f)), 0f)
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.toFloat()))
        val scalingFactor = calculateScalingFactor(
            widget.width.toDouble(),
            widget.height.toDouble(),
            abs(rotation.toDouble())
        ).toFloat()
        matrices.scale(scalingFactor, scalingFactor, 0f)
        matrices.translate(-((widget.width / 2f)), -((widget.height / 2f)), 0f)
        matrices.translate(-x.toFloat(), -y.toFloat(), 0f)
        widget.paint(context, x, y, mouseX, mouseY)
        matrices.pop()
        // outline

        if (isSelected) {
            context.drawBorder(x + PADDING, y + PADDING, this.width - 8, this.height - 8, 0xff000000.toInt())
            var rightHandleColor = 0xff000000.toInt()
            var bottomHandleColor = 0xff000000.toInt()
            var topHandleColor = 0xff000000.toInt()
            var cornerHandleColor = 0xff000000.toInt()
            if (isAtTopEdge(mouseX, mouseY)) {
                topHandleColor = 0xffff0000.toInt()
            }
            if (isAtBottomEdge(mouseX, mouseY) && isAtRightEdge(mouseX, mouseY)) {
                cornerHandleColor = 0xffff0000.toInt()
            } else {
                if (isAtRightEdge(mouseX, mouseY)) {
                    rightHandleColor = 0xffff0000.toInt()
                }
                if (isAtBottomEdge(mouseX, mouseY)) {
                    bottomHandleColor = 0xffff0000.toInt()
                }
            }

            if (canRotate) {
                context.drawBorder(x + (width / 2) - 2, y - 1, 3, 3, topHandleColor)
                context.drawVerticalLine(x + (width / 2) - 1, y, y + PADDING, topHandleColor)
            }
            context.drawBorder(x + width - (PADDING + 2), y + (height / 2) - 2, 3, 3, rightHandleColor)
            context.drawBorder(x + (width / 2) - 2, y + height - 6, 3, 3, bottomHandleColor)
            context.drawBorder(x + width - (PADDING + 2), y + height - (PADDING + 2), 3, 3, cornerHandleColor)
        } else if (outlineColor != -1) {
            context.drawBorder(x + PADDING, y + PADDING, this.width - 8, this.height - 8, outlineColor)
        }
    }

    fun isAtRightEdge(x: Int, y: Int): Boolean = x >= (width - 8) && x <= width && canResize

    fun isAtBottomEdge(x: Int, y: Int) = y >= (height - 8) && y <= height && canResize

    fun isAtTopEdge(x: Int, y: Int) = y <= 4 && y >= 0 && canRotate

}