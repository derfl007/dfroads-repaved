package at.derfl007.dfroads.renderer.util

import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RendererUtil {

    companion object {

        /**
         * Calculates the scaling factor needed for a rotated widget to fit within its parent container.
         *
         * @param width The original width of the widget.
         * @param height The original height of the widget.
         * @param rotationDegrees The rotation angle in degrees.
         * @param parentWidth The width of the parent container.
         * @param parentHeight The height of the parent container.
         * @return A scaling factor that ensures the rotated widget fits within the parent container.  Returns 1.0 if no scaling is needed.
         */
        fun calculateScalingFactor(
            width: Double,
            height: Double,
            rotationDegrees: Double,
        ): Double {
            val angleRadians = Math.toRadians(rotationDegrees)

            // Calculate the dimensions of the rotated rectangle
            val rotatedWidth = width * cos(angleRadians) + height * sin(angleRadians)
            val rotatedHeight = width * sin(angleRadians) + height * cos(angleRadians)

            // Determine if scaling is needed.  If the rotated dimensions are already within the parent, no scaling is required.
            if (rotatedWidth <= width && rotatedHeight <= height) {
                return 1.0 // No scaling needed
            }

            // Calculate the scaling factors for width and height to fit within the parent
            val scaleX = width / rotatedWidth
            val scaleY = height / rotatedHeight

            // Use the smaller scaling factor to ensure both dimensions fit
            return min(scaleX, scaleY)
        }


        fun VertexConsumer.rect(
            matrices: MatrixStack,
            x: Float = 0f,
            y: Float = 0f,
            z: Float = 0f,
            width: Float = 1f,
            height: Float = 1f,
            color: Int = -1,
            light: Int,
            overlay: Int,
            minU: Float = 0f,
            minV: Float = 0f,
            maxU: Float = 1f,
            maxV: Float = 1f,
        ) {
            val positionMatrix = matrices.peek().positionMatrix
            vertex(positionMatrix, x, y, z).normal(matrices.peek(), Vector3f()).texture(maxU, maxV).color(color)
                .light(light).overlay(overlay)
            vertex(positionMatrix, x, y + height, z).normal(matrices.peek(), Vector3f()).texture(maxU, minV).color(color)
                .light(light).overlay(overlay)
            vertex(positionMatrix, x + width, y + height, z).normal(matrices.peek(), Vector3f()).texture(minU, minV).color(color)
                .light(light).overlay(overlay)
            vertex(positionMatrix, x + width, y, z).normal(matrices.peek(), Vector3f()).texture(minU, maxV).color(color)
                .light(light).overlay(overlay)
        }

        fun VertexConsumer.outlineRect(
            matrices: MatrixStack,
            x: Float = 0f,
            y: Float = 0f,
            z: Float = 0f,
            width: Float = 1f,
            height: Float = 1f,
            outlineWidth: Float = 1f,
            color: Int = -1,
            light: Int,
            overlay: Int
        ) {
            rect(matrices, x, y, z, outlineWidth, height, color, light, overlay) // left line
            rect(matrices, x, y, z, width, outlineWidth, color, light, overlay) // top line
            rect(matrices, x + width - outlineWidth, y, z, outlineWidth, height, color, light, overlay) // right line
            rect(matrices, x, y + height, z, width, outlineWidth, color, light, overlay) // bottom line
        }

        fun VertexConsumer.nineSlice(
            matrices: MatrixStack,
            x: Float = 0f,
            y: Float = 0f,
            z: Float = 0f,
            width: Float = 1f,
            height: Float = 1f,
            color: Int = -1,
            light: Int,
            overlay: Int,
            borderTop: Float = 0f,
            borderLeft: Float = borderTop,
            borderBottom: Float = borderTop,
            borderRight: Float = borderLeft,
        ) {
            val borderLeftRelative = borderLeft
            val borderTopRelative = borderTop
            val borderRightRelative = borderRight
            val borderBottomRelative = borderBottom
            // corners
            rect(
                matrices,
                width - x - borderLeftRelative,
                height - y - borderTopRelative,
                z,
                borderLeftRelative,
                borderTopRelative,
                color,
                light,
                overlay,
                0f,
                0f,
                borderLeft,
                borderTop
            ) //top left
            rect(
                matrices,
                width - x - borderLeftRelative,
                y,
                z,
                borderLeftRelative,
                borderBottomRelative,
                color,
                light,
                overlay,
                0f,
                1f - borderBottom,
                borderLeft,
                1f
            ) // bottom left
            rect(
                matrices,
                x,
                y,
                z,
                borderRightRelative,
                borderBottomRelative,
                color,
                light,
                overlay,
                1f - borderRight,
                1f - borderBottom,
                1f,
                1f
            ) // bottom right
            rect(
                matrices,
                x,
                height - y - borderTopRelative,
                z,
                borderRightRelative,
                borderTopRelative,
                color,
                light,
                overlay,
                1f - borderRight,
                0f,
                1f,
                borderTop
            ) // top right

            // edges
            rect(
                matrices = matrices,
                x = x + width - borderLeftRelative,
                y = y + borderTopRelative,
                z = z,
                width = borderLeftRelative,
                height = height - (borderTopRelative + borderBottomRelative),
                color = color,
                light = light,
                overlay = overlay,
                minU = 0f,
                minV = borderTop,
                maxU = borderLeft,
                maxV = 1f - borderBottom
            ) // left edge
            rect(
                matrices = matrices,
                x = x + borderRightRelative,
                y = y + height - borderTopRelative,
                z = z,
                width = width - (borderLeftRelative + borderRightRelative),
                height = borderTopRelative,
                color = color,
                light = light,
                overlay = overlay,
                minU = borderLeft,
                minV = 0f,
                maxU = 1 - borderRight,
                maxV = borderTop
            ) // top line
            rect(
                matrices = matrices,
                x = x,
                y = y + borderTopRelative,
                z = z,
                width = borderRightRelative,
                height = height - (borderTopRelative + borderBottomRelative),
                color = color,
                light = light,
                overlay = overlay,
                minU = 1 - borderRight,
                minV = borderTop,
                maxU = 1f,
                maxV = 1f - borderBottom
            ) // right edge
            rect(
                matrices = matrices,
                x = x + borderRightRelative,
                y = y,
                z = z,
                width = width - (borderLeftRelative + borderRightRelative),
                height = borderBottomRelative,
                color = color,
                light = light,
                overlay = overlay,
                minU = borderBottom,
                minV = 1 - borderBottom,
                maxU = 1f - borderBottom,
                maxV = 1f
            ) // bottom line

            // center
            rect(
                matrices,
                x + borderRightRelative,
                y + borderTopRelative,
                z,
                width - (borderLeftRelative + borderRightRelative),
                height - (borderBottomRelative + borderTopRelative),
                color,
                light,
                overlay,
                borderLeft,
                borderTop,
                1f - borderRight,
                1f - borderBottom
            )
        }
    }
}