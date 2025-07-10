package at.derfl007.dfroads.model

import at.derfl007.dfroads.block.RoadBaseBlock
import at.derfl007.dfroads.util.TransformHelper
import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel
import net.fabricmc.fabric.api.renderer.v1.Renderer
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial
import net.fabricmc.fabric.api.renderer.v1.material.ShadeMode
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_LOCK_UV
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView.BAKE_NORMALIZED
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
    val sprites: Map<RoadBaseBlock.RoadTexture, List<Sprite>>,
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

        val textures = sprites[state[RoadBaseBlock.TEXTURE]] ?: sprites[RoadBaseBlock.RoadTexture.ROAD_EMPTY]!!

        if (state[RoadBaseBlock.TEXTURE].isConnectedTexture) {
            val connections = RoadBaseBlock.TextureConnection.calculate(blockView, pos)

            val textureMap = textures.associateBy {
                it.contents.id.path.split("__").last()
            }

            if (state[RoadBaseBlock.TEXTURE].straight || connections.straightConnections.any { it.value }) drawSurface(
                emitter,
                facing,
                textureMap["center_straight"]!!,
                material,
                color,
                state[RoadBaseBlock.TEXTURE].size // texture is bigger to allow connecting diagonally
            )

            if (state[RoadBaseBlock.TEXTURE].diagonal || connections.diagonalConnections.any { it.value }) drawSurface(
                emitter,
                facing,
                textureMap["center_diagonal"]!!,
                material,
                color,
                state[RoadBaseBlock.TEXTURE].size
            )

            connections.straightConnections.filter { it.value }.forEach {
                drawSurface(emitter, facing, textureMap[it.key.name.lowercase()]!!, material, color,
                    state[RoadBaseBlock.TEXTURE].size)
            }

            connections.diagonalConnections.filter { it.value }.forEach {
                val dir = "${it.key.name.lowercase()}_${it.key.rotateYClockwise().name.lowercase()}"
                drawSurface(emitter, facing, textureMap[dir]!!, material, color, state[RoadBaseBlock.TEXTURE].size)
            }
        } else {
            emitter.pushTransform(TransformHelper.rotate(textureFacing.opposite.horizontalQuarterTurns))
            if (state[RoadBaseBlock.BIG] && textures.size > 1) {
                drawSurface(emitter, facing, textures[1], material, color, 192)
            } else {
                drawSurface(emitter, facing, textures.first(), material, color)
            }
            emitter.popTransform()
        }

        emitter.popTransform()
    }

    private fun drawBack(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing, 0f, bottomHeight, 1f, topHeight, 0f)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawFront(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing.opposite, 0f, bottomHeight, 1f, slopeHeight, 0f)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawLeft(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing.rotateYCounterclockwise(), 0f, bottomHeight, 1f, topHeight, 0f)

        val posTopRight = emitter.copyPos(3, Vector3f())
        posTopRight.y = slopeHeight

        emitter.pos(3, posTopRight)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawRight(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(facing.rotateYClockwise(), 0f, bottomHeight, 1f, topHeight, 0f)

        val posTopLeft = emitter.copyPos(0, Vector3f())
        posTopLeft.y = slopeHeight

        emitter.pos(0, posTopLeft)
            .spriteBake(sprite, BAKE_LOCK_UV)
            .material(material)
            .cullFace(null)
            .emit()
    }

    private fun drawTop(emitter: QuadEmitter, facing: Direction, sprite: Sprite, material: RenderMaterial) {
        emitter.square(Direction.UP, 0f, 0f, 1f, 1f, 1 - topHeight)

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

    private fun drawSurface(
        emitter: QuadEmitter,
        facing: Direction,
        sprite: Sprite,
        material: RenderMaterial,
        color: Int,
        textureSize: Int = 64
    ) {
        val offset = ((textureSize / 64f) - 1f) / 2f
        val verticalOffset = (topHeight - slopeHeight) * offset
        emitter.color(0, color).color(1, color).color(2, color).color(3, color)
        emitter.square(Direction.UP, 0f - offset, 0f - offset, 1f + offset, 1f + offset, 1 - topHeight - 0.01f - verticalOffset)

        val bottomRightIndex = 3 - ((facing.opposite.horizontalQuarterTurns + 1) % 4)
        val bottomLeftIndex = 3 - ((facing.opposite.horizontalQuarterTurns + 2) % 4)
        val posBottomRight = emitter.copyPos(bottomRightIndex, Vector3f())
        val posBottomLeft = emitter.copyPos(bottomLeftIndex, Vector3f())
        posBottomLeft.y = slopeHeight + 0.01f - verticalOffset
        posBottomRight.y = slopeHeight + 0.01f - verticalOffset

        emitter.pos(bottomRightIndex, posBottomRight)
            .pos(bottomLeftIndex, posBottomLeft)

        for (i in 0..3) {
            val u = if (emitter.x(i) >= 0.5) emitter.x(i) - offset else emitter.x(i) + offset
            val v = if (emitter.z(i) >= 0.5) emitter.z(i) - offset else emitter.z(i) + offset
            emitter.uv(i, u, v)
        }

        emitter.spriteBake(sprite, BAKE_NORMALIZED)
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
        val texture: RoadBaseBlock.RoadTexture,
        val bottomHeight: Float,
        val topHeight: Float,
        val slopeHeight: Float
    )

    class Unbaked(
        val topHeight: Float,
        val bottomHeight: Float,
        val slopeHeight: Float,
        val baseTexture: Identifier = Identifier.of(
            "dfroads", "block/road"
        )
    ) : CustomUnbakedBlockStateModel,
        SimpleModel {

        companion object {
            val SPRITE_ID_MAP = RoadBaseBlock.RoadTexture.entries.associateWith {
                if (it.isConnectedTexture) {
                    listOf(
                        "center_straight",
                        "center_diagonal",
                        "north",
                        "east",
                        "south",
                        "west",
                        "north_east",
                        "east_south",
                        "south_west",
                        "west_north"
                    ).map { dir ->
                        SpriteIdentifier(
                            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                            Identifier.of("dfroads", "block/${it.textureName}__${dir}")
                        )
                    }
                } else if (it.canBeBig) {
                    listOf(
                        SpriteIdentifier(
                            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                            Identifier.of("dfroads", "block/${it.textureName}")
                        ),
                        SpriteIdentifier(
                            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                            Identifier.of("dfroads", "block/${it.textureName}__big")
                        )
                    )
                } else {
                    listOf(
                        SpriteIdentifier(
                            SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                            Identifier.of("dfroads", "block/${it.textureName}")
                        )
                    )
                }
            }

            fun roadSpriteId(baseTexture: Identifier) =
                SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, baseTexture)
        }

        override fun codec(): MapCodec<out CustomUnbakedBlockStateModel?>? {
            return MapCodec.unit(this)
        }

        override fun bake(baker: Baker): BlockStateModel {
            val spriteMap: Map<RoadBaseBlock.RoadTexture, List<Sprite>> = SPRITE_ID_MAP.map { (key, value) ->
                Pair(
                    key,
                    value.map { baker.spriteGetter[it, this@Unbaked] }.toList()
                )
            }.toMap()
            val roadSprite = baker.spriteGetter[roadSpriteId(baseTexture), this]

            return RoadBlockStateModel(spriteMap, roadSprite, topHeight, bottomHeight, slopeHeight)
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