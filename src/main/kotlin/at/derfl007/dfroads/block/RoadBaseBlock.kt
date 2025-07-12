package at.derfl007.dfroads.block

import at.derfl007.dfroads.util.Color
import at.derfl007.dfroads.util.HORIZONTAL_DIRECTIONS
import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

abstract class RoadBaseBlock(private val outlineShape: VoxelShape, settings: Settings) : HorizontalFacingBlock(
    settings
) {
    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.NORTH).with(COLOR, Color.WHITE)
            .with(TEXTURE_FACING, Direction.NORTH).with(TEXTURE, RoadTexture.ROAD_EMPTY).with(BIG, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, COLOR, TEXTURE_FACING, TEXTURE, BIG)
    }

    override fun isTransparent(state: BlockState?) = true

    override fun isShapeFullCube(state: BlockState?, world: BlockView?, pos: BlockPos?) = false

    override fun hasSidedTransparency(state: BlockState?) = true

    override fun getOutlineShape(
        state: BlockState, world: BlockView?, pos: BlockPos?, context: ShapeContext?
    ): VoxelShape? {
        val facing = state[FACING]
        return VoxelShapes.createHorizontalFacingShapeMap(outlineShape)[facing]
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return super.getPlacementState(ctx)?.with(FACING, ctx?.horizontalPlayerFacing)?.with(COLOR, Color.WHITE)
            ?.with(TEXTURE_FACING, ctx?.horizontalPlayerFacing)?.with(TEXTURE, RoadTexture.ROAD_EMPTY)?.with(BIG, false)
    }

    class TextureConnection {

        val straightConnections = HORIZONTAL_DIRECTIONS.associateWith { false }.toMutableMap()
        val diagonalConnections = HORIZONTAL_DIRECTIONS.associateWith { false }
            .toMutableMap() // north = north east, east = south east, south = south west, west = north west

        companion object {

            /**
             * Calculates straight and diagonal connections based on adjacent blocks
             *
             * If the current block's texture is straight only, it will only connect to other straight textures.
             * If the current block's texture is diagonal only, it will only connect to other diagonal textures.
             * If the current block's texture is both, it will connect to either straight or diagonal textures.
             */
            fun calculate(world: BlockView, blockPos: BlockPos): TextureConnection {
                val connection = TextureConnection()

                HORIZONTAL_DIRECTIONS.forEach {
                    val state = world.getBlockState(blockPos)
                    if (state[TEXTURE].straight) {
                        val offsetPos = blockPos.offset(it)
                        val offsetState = world.getBlockState(offsetPos)
                        val offsetDownState = world.getBlockState(offsetPos.down())
                        val offsetUpState = world.getBlockState(offsetPos.up())
                        connection.straightConnections[it] =
                            (offsetState.block is RoadBaseBlock && (offsetState[TEXTURE].straight || offsetState[TEXTURE].connectToLines)) || (offsetDownState.block is RoadBaseBlock && (offsetDownState[TEXTURE].straight || offsetDownState[TEXTURE].connectToLines)) || (offsetUpState.block is RoadBaseBlock && (offsetUpState[TEXTURE].straight || offsetUpState[TEXTURE].connectToLines))
                    }
                    if (state[TEXTURE].diagonal) {
                        val offsetPos = blockPos.offset(it).offset(it.rotateYClockwise())
                        val offsetState = world.getBlockState(offsetPos)
                        val offsetDownState = world.getBlockState(offsetPos.down())
                        val offsetUpState = world.getBlockState(offsetPos.up())
                        connection.diagonalConnections[it] =
                            (offsetState.block is RoadBaseBlock && (offsetState[TEXTURE].diagonal || offsetState[TEXTURE].connectToLines)) || (offsetDownState.block is RoadBaseBlock && (offsetDownState[TEXTURE].diagonal || offsetDownState[TEXTURE].connectToLines)) || (offsetUpState.block is RoadBaseBlock && (offsetUpState[TEXTURE].diagonal || offsetUpState[TEXTURE].connectToLines))
                    }
                }

                return connection
            }
        }

    }

    // Note that the enum value names need to be uppercase versions of the texture names (without .png)
    enum class RoadTexture(
        val straight: Boolean = false,
        val diagonal: Boolean = false,
        val texture: String? = null,
        val canBeBig: Boolean = false,
        val connectToLines: Boolean = false,
        val size: Int = 64
    ) :
        StringIdentifiable {
        ROAD_EMPTY,
        ROAD_ARROW_S(canBeBig = true),
        ROAD_ARROW_L(canBeBig = true),
        ROAD_ARROW_R(canBeBig = true),
        ROAD_ARROW_SL(canBeBig = true),
        ROAD_ARROW_SR(canBeBig = true),
        ROAD_ARROW_RL(canBeBig = true),
        ROAD_ARROW_SRL(canBeBig = true),
        ROAD_BUS(canBeBig = true),
        ROAD_TAXI(canBeBig = true),
        ROAD_PARKING(canBeBig = true),
        ROAD_DISABLED_PARKING(canBeBig = true),
        ROAD_CROSSWALK,
        ROAD_CROSSWALK_DIAGONAL_CENTER,
        ROAD_CROSSWALK_DIAGONAL_END,
        ROAD_CROSSWALK_DIAGONAL_SIDE,
        ROAD_LINE_SINGLE(straight = true, size = 76),
        ROAD_LINE_SINGLE_DIAGONAL(diagonal = true, texture = "road_line_single", size = 76),
        ROAD_LINE_SINGLE_BOTH(straight = true, diagonal = true, texture = "road_line_single", size = 76),
        ROAD_LINE_MERGE(connectToLines = true),
        ROAD_LINE_DOUBLE(straight = true, size = 86),
        ROAD_LINE_DOUBLE_DIAGONAL(diagonal = true, texture = "road_line_double", size = 86),
        ROAD_LINE_HALF_LEFT(straight = true, size = 86),
        ROAD_LINE_HALF_LEFT_DIAGONAL(diagonal = true, texture = "road_line_half_left", size = 86),
        ROAD_LINE_HALF_RIGHT(straight = true, size = 86),
        ROAD_LINE_HALF_RIGHT_DIAGONAL(diagonal = true, texture = "road_line_half_right", size = 86),
        ROAD_WHITE(connectToLines = true),
        ROAD_WHITE_HALF(connectToLines = true),
        ROAD_WHITE_QUARTER(connectToLines = true);

        val textureName: String = texture ?: asString()

        val isConnectedTexture = straight || diagonal

        override fun toString() = super.toString().lowercase()

        override fun asString() = toString()

        companion object {
            val CODEC = StringIdentifiable.createCodec(RoadTexture::values);
        }
    }

    companion object {
        val COLOR: EnumProperty<Color> = EnumProperty.of("color", Color::class.java)
        val TEXTURE_FACING: EnumProperty<Direction> = EnumProperty.of(
            "texture_facing", Direction::class.java, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
        )
        val TEXTURE: EnumProperty<RoadTexture> = EnumProperty.of("texture", RoadTexture::class.java)
        val BIG: BooleanProperty = BooleanProperty.of("big")

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

    override fun isTransparent(state: BlockState?) = false
    override fun isShapeFullCube(state: BlockState?, world: BlockView?, pos: BlockPos?) = false
    override fun hasSidedTransparency(state: BlockState?) = false

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