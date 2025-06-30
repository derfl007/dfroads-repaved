package at.derfl007.dfroads.renderer

import at.derfl007.dfroads.Constants
import at.derfl007.dfroads.DFRoads
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity
import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity.SignElement.Type.*
import at.derfl007.dfroads.renderer.util.RendererUtil.Companion.calculateScalingFactor
import at.derfl007.dfroads.renderer.util.RendererUtil.Companion.nineSlice
import at.derfl007.dfroads.renderer.util.RendererUtil.Companion.outlineRect
import at.derfl007.dfroads.renderer.util.RendererUtil.Companion.rect
import at.derfl007.dfroads.util.Color
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.texture.Scaling
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import kotlin.math.abs

class ComplexRoadSignBlockEntityRenderer(val context: BlockEntityRendererFactory.Context) :
    BlockEntityRenderer<ComplexRoadSignBlockEntity> {
    override fun render(
        entity: ComplexRoadSignBlockEntity,
        tickProgress: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        cameraPos: Vec3d
    ) {
        matrices.push()

        val texture = DFRoads.id("textures/gui/sprites/${Constants.complexSignTextures[entity.backgroundTexture]}.png")
        val facing = entity.cachedState[HorizontalFacingBlock.FACING]

        matrices.translate(0.5, 0.5, 0.5) // move to center of block

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.opposite.positiveHorizontalDegrees))

        // move closer to player
        matrices.translate(0.0, 0.0, -0.3125)

        // scale (centered) to size
//        matrices.scale(entity.width, entity.height, 1f)

        // move back down to bottom left
        matrices.translate(-entity.width / 2.0, -entity.height / 2.0, 0.0)

        val buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture))

        val sprite = MinecraftClient.getInstance().guiAtlasManager.getSprite(DFRoads.id(Constants.complexSignTextures[entity.backgroundTexture]))
        val scaling = MinecraftClient.getInstance().guiAtlasManager.getScaling(sprite)
        if (scaling.type == Scaling.Type.NINE_SLICE && scaling is Scaling.NineSlice) {
            val borderTop = scaling.border.top / scaling.height.toFloat()
            val borderLeft = scaling.border.left / scaling.width.toFloat()
            val borderBottom = scaling.border.bottom / scaling.height.toFloat()
            val borderRight = scaling.border.right / scaling.width.toFloat()

            buffer.nineSlice(
                matrices,
                width = entity.width,
                height = entity.height,
                light = light,
                overlay = overlay,
                borderTop = borderTop,
                borderLeft = borderLeft,
                borderBottom = borderBottom,
                borderRight = borderRight,
            )

        }


        matrices.translate(entity.width / 2.0, entity.height / 2.0, 0.0)
        matrices.scale(entity.width, entity.height, 1f)
        matrices.translate(-0.5, -0.5, 0.0)

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f))
        matrices.translate(-1.0, 0.0, -0.001)

        buffer.rect(matrices, light = light, overlay = overlay)

        matrices.translate(1.0, 0.0, 0.002)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-180f))
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f))
        matrices.scale(1 / entity.width, 1 / entity.height, 1f)
        matrices.translate(-entity.width, -entity.height, 0f)

        entity.elements.forEach { renderSignElement(it, entity, matrices, vertexConsumers, light, overlay) }

        matrices.pop()
    }

    fun renderSignElement(
        element: ComplexRoadSignBlockEntity.SignElement,
        entity: ComplexRoadSignBlockEntity,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
    ) {
        if (element.borderColor != Color.NONE) {
            val buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(DFRoads.id("textures/block/white.png")))
            val width =
                if (element.type == TEXT) context.textRenderer.getWidth(element.text) * (1 / 9f) * element.height else element.width
            buffer.outlineRect(
                matrices,
                element.x,
                element.y,
                -0.001f,
                width,
                element.height,
                0.01f,
                element.borderColor.argb(),
                light,
                overlay
            )
        }
        matrices.push()
        matrices.translate(element.x, element.y, 0f)
        matrices.translate(element.width / 2.0, element.height / 2.0, 0.0)
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(element.rotation.toFloat()))
        val scalingFactor = calculateScalingFactor(
            element.width.toDouble(),
            element.height.toDouble(),
            abs(element.rotation.toDouble())
        ).toFloat()
        matrices.scale(scalingFactor, scalingFactor, 0f)
        matrices.translate(-element.width / 2.0, -element.height / 2.0, 0.0)
        matrices.translate(-element.x, -element.y, 0f)

        when (element.type) {
            TEXT -> renderTextElement(
                element,
                entity,
                matrices,
                vertexConsumers,
                light,
                overlay
            )

            ARROW -> renderArrowElement(
                element,
                entity,
                matrices,
                vertexConsumers,
                light,
                overlay
            )

            ICON -> renderIconElement(
                element,
                entity,
                matrices,
                vertexConsumers,
                light,
                overlay
            )

            BOX -> renderContainerElement(
                element,
                entity,
                matrices,
                vertexConsumers,
                light,
                overlay
            )
        }
        matrices.pop()
        matrices.translate(0f, 0f, -0.0001f)

    }

    fun renderTextElement(
        element: ComplexRoadSignBlockEntity.SignElement,
        entity: ComplexRoadSignBlockEntity,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.scale(
            (1 / 9f) * (element.height * 0.9f),
            (1 / 9f) * (element.height * 0.9f),
            0f
        ) // multiply both by height since we only care about font size
        context.textRenderer.draw(
            element.text,
            element.x * 9f * (1 / (element.height * 0.9f) + 0.2f),
            element.y * 9f * (1 / (element.height * 0.9f) + 0.2f),
            element.color.argb(),
            false,
            matrices.peek().positionMatrix,
            vertexConsumers,
            TextRenderer.TextLayerType.NORMAL,
            0,
            light
        )
        matrices.scale(9f * (1 / (element.height * 0.9f)), 9f * (1 / (element.height * 0.9f)), 0f)
    }

    fun renderIconElement(
        element: ComplexRoadSignBlockEntity.SignElement,
        entity: ComplexRoadSignBlockEntity,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(DFRoads.id("textures/block/${Constants.iconTextures[element.iconTexture]}.png")))
        buffer.rect(
            matrices,
            element.x,
            element.y,
            0f,
            element.width,
            element.height,
            element.color.argb(),
            light,
            overlay,
            1f,
            1f,
            0f,
            0f
        )
    }

    fun renderArrowElement(
        element: ComplexRoadSignBlockEntity.SignElement,
        entity: ComplexRoadSignBlockEntity,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer =
            vertexConsumers.getBuffer(RenderLayer.getEntityCutout(DFRoads.id("textures/gui/sprites/road_sign_arrow.png")))
        buffer.rect(
            matrices,
            element.x,
            element.y,
            0f,
            element.width,
            element.width,
            element.color.argb(),
            light,
            overlay,
            minV = 0.9f,
            maxV = 0f,
        )
        buffer.rect(
            matrices,
            element.x,
            element.y + (element.width),
            0f,
            element.width,
            element.height - element.width,
            element.color.argb(),
            light,
            overlay,
            minV = 0.9f
        )
    }

    fun renderContainerElement(
        element: ComplexRoadSignBlockEntity.SignElement,
        entity: ComplexRoadSignBlockEntity,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(DFRoads.id("textures/block/white.png")))
        buffer.rect(
            matrices,
            element.x,
            element.y,
            0f,
            element.width,
            element.height,
            element.color.argb(),
            light,
            overlay
        )
    }
}