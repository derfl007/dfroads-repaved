package at.derfl007.dfroads.renderer

import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.block.PedestrianTrafficLightBlock
import at.derfl007.dfroads.blockentity.TrafficLightBlockEntity
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f

class TrafficLightBlockEntityRenderer(val context: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<TrafficLightBlockEntity> {
    override fun render(
        entity: TrafficLightBlockEntity,
        tickProgress: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d
    ) {
        fun renderTexture(model: Matrix4f, buffer: VertexConsumer) {
            buffer.vertex(model, 0f, 0f, 0f).texture(4 / 16f, 4 / 16f).color(-1).normal(matrices.peek(), Vector3f()).light(1)
                .overlay(overlay)
            buffer.vertex(model, 0f, 4f / 16, 0f).texture(4 / 16f, 0f).color(-1).normal(matrices.peek(), Vector3f())
                .light(1)
                .overlay(overlay)
            buffer.vertex(model, 4f / 16, 4f / 16, 0f).texture(0f, 0f).color(-1).normal(matrices.peek(), Vector3f())
                .light(1)
                .overlay(overlay)
            buffer.vertex(model, 4f / 16, 0f, 0f).texture(0f, 4 / 16f).color(-1).normal(matrices.peek(), Vector3f())
                .light(1)
                .overlay(overlay)
        }

        val facing = entity.cachedState[HorizontalFacingBlock.FACING]

        matrices.push()

        matrices.translate(0.5, 0.5, 0.5)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.opposite.positiveHorizontalDegrees))
        matrices.translate(-0.5, -0.5, -0.5)

        entity.getCurrentPhase()?.let { currentPhase ->

            if (currentPhase.isRedOn) {
                matrices.push()
                if (entity.cachedState.block is PedestrianTrafficLightBlock) {
                    matrices.translate(6.0 / 16, 6.0 / 16, 4.69 / 16)
                } else {
                    matrices.translate(6.0 / 16, 11.0 / 16, 4.69 / 16)
                }
                val model: Matrix4f = matrices.peek().positionMatrix
                val buffer =
                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(DFRoads.id("textures/block/traffic_light_red_on.png")))

                renderTexture(model, buffer)
                matrices.pop()
            }

            if (currentPhase.isYellowOn && entity.cachedState.block !is PedestrianTrafficLightBlock) {
                matrices.push()
                matrices.translate(6.0 / 16, 6.0 / 16, 4.69 / 16)
                val model: Matrix4f = matrices.peek().positionMatrix
                val buffer =
                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(DFRoads.id("textures/block/traffic_light_yellow_on.png")))

                renderTexture(model, buffer)
                matrices.pop()
            }

            if (currentPhase.isGreenOn) {
                matrices.push()
                matrices.translate(6.0 / 16, 1.0 / 16, 4.69 / 16)
                val model: Matrix4f = matrices.peek().positionMatrix
                val buffer =
                    vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(DFRoads.id("textures/block/traffic_light_green_on.png")))

                renderTexture(model, buffer)
                matrices.pop()
            }
        }

        matrices.pop()
    }
}