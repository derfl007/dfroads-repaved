package at.derfl007.dfroads.block

import at.derfl007.dfroads.Constants.roadTextures
import at.derfl007.dfroads.util.Color
import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

abstract class RoadBaseBlock(private val outlineShape: VoxelShape, settings: Settings) : HorizontalFacingBlock(
    settings
) {
    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.NORTH).with(COLOR, Color.WHITE).with(TEXTURE_FACING, Direction.NORTH).with(TEXTURE, 0)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, COLOR, TEXTURE_FACING, TEXTURE)
    }

    override fun isTransparent(state: BlockState?) = true

    override fun isShapeFullCube(state: BlockState?, world: BlockView?, pos: BlockPos?) = false

    override fun hasSidedTransparency(state: BlockState?) = true

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape? {
        val facing = state[FACING]
        return VoxelShapes.createHorizontalFacingShapeMap(outlineShape)[facing]
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return super.getPlacementState(ctx)?.with(FACING, ctx?.horizontalPlayerFacing)?.with(COLOR, Color.WHITE)?.with(TEXTURE_FACING, ctx?.horizontalPlayerFacing)?.with(TEXTURE, 0)
    }

    companion object {
        val COLOR: EnumProperty<Color> = EnumProperty.of("color", Color::class.java)
        val TEXTURE_FACING: EnumProperty<Direction> = EnumProperty.of("texture_facing", Direction::class.java, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
        val TEXTURE: IntProperty = IntProperty.of("texture", 0, roadTextures.size - 1)

        private const val FULL_HEIGHT = 16f / 16f
        const val MIN_Y = FULL_HEIGHT - 1f

        /**
         * Calculates the normalized Y coordinate for the given offset.
         * @param offset The offset to calculate the Y coordinate for.
         * @return The Y coordinate normalized to the range `[0, 1]`.
         */
        fun calculateYNormalized(offset: Float): Float {
            return MIN_Y + offset
        }

        /**
         *  Calculates the Y coordinate for the given offset.
         * @param offset The offset to calculate the Y coordinate for.
         * @return The Y coordinate in the range `[0, 16]`. `MIN_Y * 16.0` if offset is empty or 0
         */
        fun calculateY(offset: Float = 0f): Double {
            return calculateYNormalized(offset) * 16.0
        }
    }
}

// Specific block classes

private val ROAD_BLOCK_SHAPE =
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 0.0, 16.0, RoadBaseBlock.calculateY(1f), 16.0)

class RoadFullBlock(settings: Settings) : RoadBaseBlock(ROAD_BLOCK_SHAPE, settings) {
    override fun getCodec(): MapCodec<out HorizontalFacingBlock?> = createCodec { settings -> RoadFullBlock(settings) }
}

private val ROAD_SLAB_SHAPE =
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 0.0, 16.0, RoadBaseBlock.calculateY(0.5f), 16.0)

class RoadSlabBlock(settings: Settings) : RoadBaseBlock(ROAD_SLAB_SHAPE, settings) {
    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? = createCodec { settings -> RoadSlabBlock(settings) }
}

private val ROAD_FULL_SLOPE_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 0.0, 16.0, RoadBaseBlock.calculateY(1f), 4.0),
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 4.0, 16.0, (RoadBaseBlock.MIN_Y + 0.75) * 16.0, 8.0),
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 8.0, 16.0, RoadBaseBlock.calculateY(0.5f), 12.0),
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 12.0, 16.0, RoadBaseBlock.calculateY(0.25f), 16.0)
)

class RoadFullSlopeBlock(settings: Settings) : RoadBaseBlock(ROAD_FULL_SLOPE_SHAPE, settings) {
    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? =
        createCodec { settings -> RoadFullSlopeBlock(settings) }
}

private val ROAD_TOP_SLOPE_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 0.0, 16.0, RoadBaseBlock.calculateY(1f), 8.0),
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 8.0, 16.0, (RoadBaseBlock.MIN_Y + 0.75) * 16.0, 16.0)
)

class RoadTopSlopeBlock(settings: Settings) : RoadBaseBlock(ROAD_TOP_SLOPE_SHAPE, settings) {
    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? =
        createCodec { settings -> RoadTopSlopeBlock(settings) }
}

private val ROAD_BOTTOM_SLOPE_SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 0.0, 16.0, RoadBaseBlock.calculateY(0.5f), 8.0),
    Block.createCuboidShape(0.0, RoadBaseBlock.calculateY(), 8.0, 16.0, RoadBaseBlock.calculateY(0.25f), 16.0)
)

class RoadBottomSlopeBlock(settings: Settings) : RoadBaseBlock(ROAD_BOTTOM_SLOPE_SHAPE, settings) {
    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? =
        createCodec { settings -> RoadBottomSlopeBlock(settings) }
}