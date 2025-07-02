package at.derfl007.dfroads.renderer

import at.derfl007.dfroads.Constants
import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.block.LedSignBlock
import at.derfl007.dfroads.block.RedstoneTransmitterBlock
import at.derfl007.dfroads.blockentity.RoadSignBlockEntity
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f

class RoadSignBlockEntityRenderer(val context: BlockEntityRendererFactory.Context): BlockEntityRenderer<RoadSignBlockEntity> {
    override fun render(
        entity: RoadSignBlockEntity,
        tickProgress: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d
    ) {
        matrices.push()

        val texture = DFRoads.id("textures/block/${Constants.signTextures[entity.texture]}.png")
        val isLedSign = entity.cachedState.block is LedSignBlock
        val size =
            if (isLedSign) 0.75f
            else when (entity.size) {
                0 -> 0.5f
                1 -> 1f
                2 -> 2f
                3 -> 4f
                else -> 1f
            }
        val facing = entity.cachedState[HorizontalFacingBlock.FACING]

        val zOffset = if (isLedSign) -2.01 else -5.0

        matrices.translate(0.5, 0.5, 0.5)

        // south = 0, west = 270, north = 180, east = 90

        when(facing) {
            Direction.NORTH, Direction.SOUTH ->
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.opposite.positiveHorizontalDegrees))
            Direction.EAST, Direction.WEST ->
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.positiveHorizontalDegrees))
            else -> IllegalStateException("Block facing can only be horizontal, was $facing")
        }
        matrices.translate(0.0, 0.0, zOffset / 16.0)
        matrices.scale(size, size, 1f)
        matrices.translate(-0.5, -0.5, 0.0)

        if (isLedSign) {
            matrices.translate(0.0, 0.0, -0.2 / 16.0)
            val buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(DFRoads.id("textures/block/led_sign_overlay.png")))

            val model = matrices.peek().positionMatrix
            buffer.vertex(model, 0f, 0f, 0f).texture(1f, 1f).color(-1).normal(matrices.peek(), Vector3f()).light(light)
                .overlay(overlay)
            buffer.vertex(model, 0f, 1f, 0f).texture(1f, 0f).color(-1).normal(matrices.peek(), Vector3f()).light(light)
                .overlay(overlay)
            buffer.vertex(model, 1f, 1f, 0f).texture(0f, 0f).color(-1).normal(matrices.peek(), Vector3f()).light(light)
                .overlay(overlay)
            buffer.vertex(model, 1f, 0f, 0f).texture(0f, 1f).color(-1).normal(matrices.peek(), Vector3f()).light(light)
                .overlay(overlay)

            matrices.translate(0.0, 0.0, 0.2 / 16.0)
        }

        val buffer = vertexConsumers.getBuffer(if (isLedSign && entity.cachedState[RedstoneTransmitterBlock.Companion.POWER] == 0) RenderLayer.getGuiTextured(texture) else RenderLayer.getEntityCutout(texture))

        var model: Matrix4f? = matrices.peek().positionMatrix

        buffer.vertex(model, 0f, 0f, 0f).texture(1f, 1f).color(-1).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)
        buffer.vertex(model, 0f, 1f, 0f).texture(1f, 0f).color(-1).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)
        buffer.vertex(model, 1f, 1f, 0f).texture(0f, 0f).color(-1).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)
        buffer.vertex(model, 1f, 0f, 0f).texture(0f, 1f).color(-1).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f))
        matrices.translate(-1.0, 0.0, -0.001)

        model = matrices.peek().positionMatrix

        buffer.vertex(model, 0f, 0f, 0f).texture(1f, 1f).color(0xFF000000.toInt()).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)
        buffer.vertex(model, 0f, 1f, 0f).texture(1f, 0f).color(0xFF000000.toInt()).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)
        buffer.vertex(model, 1f, 1f, 0f).texture(0f, 0f).color(0xFF000000.toInt()).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)
        buffer.vertex(model, 1f, 0f, 0f).texture(0f, 1f).color(0xFF000000.toInt()).normal(matrices.peek(), Vector3f()).light(light).overlay(overlay)

        matrices.pop()
    }
}