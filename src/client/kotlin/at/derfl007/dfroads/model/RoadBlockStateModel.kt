package at.derfl007.dfroads.model

import at.derfl007.dfroads.Constants.roadTextures
import at.derfl007.dfroads.block.RoadBaseBlock
import at.derfl007.dfroads.util.TransformHelper
import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel
import net.fabricmc.fabric.api.renderer.v1.Renderer
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_LOCK_UV
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.client.render.model.*
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockRenderView
import org.joml.Vector3f
import java.util.function.Predicate

class RoadBlockStateModel(
    val sprites: Array<Sprite>,
    val roadSprite: Sprite,
    val topHeight: Float = 1f,
    val bottomHeight: Float = 0f,
    val slopeHeight: Float = topHeight
) : BlockStateModel {

    override fun emitQuads(
        emitter: QuadEmitter,
        blockView: BlockRenderView,
        pos: BlockPos,
        state: BlockState,
        random: Random,
        cullTest: Predicate<Direction?>
    ) {
        val facing = state[HorizontalFacingBlock.FACING]
        val textureFacing = state[RoadBaseBlock.TEXTURE_FACING]
        val texture = sprites[state[RoadBaseBlock.TEXTURE]]
        val color = state[RoadBaseBlock.COLOR].argb()

        val finder = Renderer.get().materialFinder()
        val material = finder.shadeMode(ShadeMode.ENHANCED).emissive(false).blendMode(BlendMode.CUTOUT).find()

        emitter.pushTransform(TransformHelper.translateY(RoadBaseBlock.MIN_Y))

        emitter.color(0, -1).color(1, -1).color(2, -1).color(3, -1)
        drawBack(emitter, facing, roadSprite, material)
        drawFront(emitter, facing, roadSprite, material)
        drawLeft(emitter, facing, roadSprite, material)
        drawRight(emitter, facing, roadSprite, material)
        drawTop(emitter, facing, roadSprite, material)
        drawBottom(emitter, roadSprite, material)

        emitter.pushTransform(TransformHelper.rotate(textureFacing.opposite.horizontalQuarterTurns))

        emitter.color(0, color).color(1, color).color(2, color).color(3, color)
        drawSurface(emitter, facing, texture, material)

        emitter.popTransform()

        emitter.popTransform()
    }

    private fun drawBack(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing, 0.0001f, bottomHeight, 0.9999f, topHeight, 0.0001f)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawFront(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing.opposite, 0.0001f, bottomHeight, 0.9999f, slopeHeight, 0.0001f)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawLeft(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing.rotateYCounterclockwise(), 0.0001f, bottomHeight, 0.9999f, topHeight, 0.0001f)

        val posTopRight = emitter.copyPos(3, Vector3f())
        posTopRight.y = slopeHeight

        emitter.pos(3, posTopRight)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawRight(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing.rotateYClockwise(), 0.0001f, bottomHeight, 0.9999f, topHeight, 0.0001f)

        val posTopLeft = emitter.copyPos(0, Vector3f())
        posTopLeft.y = slopeHeight

        emitter.pos(0, posTopLeft)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawTop(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(Direction.UP, 0.0001f, 0.0001f, 0.9999f, 0.9999f, 1 - topHeight)

        val bottomRightIndex = 3 - ((facing.opposite.horizontalQuarterTurns + 1) % 4)
        val bottomLeftIndex = 3 - ((facing.opposite.horizontalQuarterTurns + 2) % 4)
        val posBottomRight = emitter.copyPos(bottomRightIndex, Vector3f())
        val posBottomLeft = emitter.copyPos(bottomLeftIndex, Vector3f())
        posBottomLeft.y = slopeHeight
        posBottomRight.y = slopeHeight

        emitter.pos(bottomRightIndex, posBottomRight)
            .pos(bottomLeftIndex, posBottomLeft)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawSurface(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(Direction.UP, 0.0001f, 0.0001f, 0.9999f, 0.9999f, 1 - topHeight - 0.0001f)

        val bottomRightIndex = 3 - ((facing.opposite.horizontalQuarterTurns + 1) % 4)
        val bottomLeftIndex = 3 - ((facing.opposite.horizontalQuarterTurns + 2) % 4)
        val posBottomRight = emitter.copyPos(bottomRightIndex, Vector3f())
        val posBottomLeft = emitter.copyPos(bottomLeftIndex, Vector3f())
        posBottomLeft.y = slopeHeight + 0.0001f
        posBottomRight.y = slopeHeight + 0.0001f

        emitter.pos(bottomRightIndex, posBottomRight)
            .pos(bottomLeftIndex, posBottomLeft)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawBottom(emitter: QuadEmitter, sprite: Sprite, material: RenderMaterial) {
        emitter.square(Direction.DOWN, 0f, 0f, 1f, 1f, 0f)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .emit()
    }

    override fun createGeometryKey(
        blockView: BlockRenderView,
        pos: BlockPos,
        state: BlockState,
        random: Random
    ): Any? {
        return RoadBlockGeometryKey(
            state[HorizontalFacingBlock.FACING],
            state[RoadBaseBlock.TEXTURE_FACING],
            state[RoadBaseBlock.COLOR].argb(),
            state[RoadBaseBlock.TEXTURE],
            bottomHeight,
            topHeight,
            slopeHeight,
        )
    }

    override fun addParts(random: Random?, parts: List<BlockModelPart?>?) {
        // not needed
    }

    override fun particleSprite() = roadSprite

    data class RoadBlockGeometryKey(
        val facing: Direction,
        val textureFacing: Direction,
        val color: Int,
        val texture: Int,
        val bottomHeight: Float,
        val topHeight: Float,
        val slopeHeight: Float
    )

    class Unbaked(val topHeight: Float, val bottomHeight: Float, val slopeHeight: Float) : CustomUnbakedBlockStateModel,
        SimpleModel {

        companion object {
            val SPRITE_IDS = Array(roadTextures.size) {
                SpriteIdentifier(
                    SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                    Identifier.of("dfroads", roadTextures[it])
                )
            }
            val ROAD_SPRITE_ID =
                SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of("dfroads", "block/road"))
        }

        override fun codec(): MapCodec<out CustomUnbakedBlockStateModel?>? {
            return MapCodec.unit(this)
        }

        override fun bake(baker: Baker): BlockStateModel {
            val sprites = Array(SPRITE_IDS.size) {
                baker.spriteGetter[SPRITE_IDS[it], this]
            }
            val roadSprite = baker.spriteGetter[ROAD_SPRITE_ID, this]

            return RoadBlockStateModel(sprites, roadSprite, topHeight, bottomHeight, slopeHeight)
        }

        override fun resolve(resolver: ResolvableModel.Resolver?) {
            // nothing?
        }

        override fun name(): String? {
            return this::class.java.name
        }

        override fun toString(): String {
            return "RoadBlockStateModel.Unbaked(${topHeight}, ${bottomHeight}, ${slopeHeight})"
        }

    }
}