package at.derfl007.dfroads.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

/**
 * @param shouldFrontConnect Whether the front of the block should connect to other [SignPostBlock] instances.
 */
open class SignPostBlock(settings: Settings, val shouldFrontConnect: Boolean = true): RedstoneTransmitterBlock(settings) {

    init {
        defaultState = stateManager.defaultState
            .with(FACING, Direction.NORTH)
            .with(POWER, 0)
            .with(UP, false)
            .with(DOWN, false)
            .with(NORTH, false)
            .with(SOUTH, false)
            .with(EAST, false)
            .with(WEST, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, POWER, UP, DOWN, NORTH, SOUTH, EAST, WEST)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        world: WorldView,
        tickView: ScheduledTickView,
        pos: BlockPos,
        direction: Direction,
        neighborPos: BlockPos,
        neighborState: BlockState,
        random: Random
    ): BlockState? {
        val facing = world.getBlockState(pos)[FACING]
        return state
            .with(UP, canConnectTo(world, pos, Direction.UP, facing))
            .with(DOWN, canConnectTo(world, pos, Direction.DOWN, facing))
            .with(NORTH, canConnectTo(world, pos, Direction.NORTH, facing))
            .with(SOUTH, canConnectTo(world, pos, Direction.SOUTH, facing))
            .with(EAST, canConnectTo(world, pos, Direction.EAST, facing))
            .with(WEST, canConnectTo(world, pos, Direction.WEST, facing))
    }

    private fun canConnectTo(world: BlockView, pos: BlockPos, offset: Direction, facing: Direction): Boolean {
        if (!shouldFrontConnect && facing == offset) return false
        val state = world.getBlockState(pos.offset(offset))
        val block = state.block
        if (block is SignPostBlock) {
            return block.shouldFrontConnect || state[FACING] != offset.opposite
        }
        return state.isFullCube(world, pos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val world = ctx.world
        val pos = ctx.blockPos
        val facing = ctx.horizontalPlayerFacing.opposite
        return super.getPlacementState(ctx)
            ?.with(UP, canConnectTo(world, pos, Direction.UP, facing))
            ?.with(DOWN, canConnectTo(world, pos, Direction.DOWN, facing))
            ?.with(NORTH, canConnectTo(world, pos, Direction.NORTH, facing))
            ?.with(SOUTH, canConnectTo(world, pos, Direction.SOUTH, facing))
            ?.with(EAST, canConnectTo(world, pos, Direction.EAST, facing))
            ?.with(WEST, canConnectTo(world, pos, Direction.WEST, facing))
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        val state = super.rotate(state, rotation)
        return when(rotation) {
            BlockRotation.CLOCKWISE_180 -> state.with(NORTH, state[SOUTH]).with(EAST, state[WEST]).with(SOUTH, state[NORTH]).with(WEST, state[EAST])
            BlockRotation.COUNTERCLOCKWISE_90 -> state.with(NORTH, state[EAST]).with(EAST, state[SOUTH]).with(SOUTH, state[WEST]).with(WEST, state[NORTH])
            BlockRotation.CLOCKWISE_90 -> state.with(NORTH, state[WEST]).with(EAST, state[NORTH]).with(SOUTH, state[EAST]).with(WEST, state[SOUTH])
            else -> state!!
        }
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        val state = super.mirror(state, mirror)
        return when (mirror) {
            BlockMirror.LEFT_RIGHT -> state.with(NORTH, state[SOUTH]).with(SOUTH, state[NORTH])
            BlockMirror.FRONT_BACK -> state.with(EAST, state[WEST]).with(WEST, state[EAST])
            else -> state
        }
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        val center = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0)
        val up = createCuboidShape(6.0, 10.0, 6.0, 10.0, 22.0, 10.0)
        val down = createCuboidShape(6.0, -6.0, 6.0, 10.0, 6.0, 10.0)
        val north = createCuboidShape(6.0, 6.0, -6.0, 10.0, 10.0, 6.0)
        val south = createCuboidShape(6.0, 6.0, 10.0, 10.0, 10.0, 22.0)
        val east = createCuboidShape(10.0, 6.0, 6.0, 22.0, 10.0, 10.0)
        val west = createCuboidShape(-6.0, 6.0, 6.0, 6.0, 10.0, 10.0)

        var full = center

        if (state[UP]) full = VoxelShapes.union(full, up)
        if (state[DOWN]) full = VoxelShapes.union(full, down)
        if (state[NORTH]) full = VoxelShapes.union(full, north)
        if (state[SOUTH]) full = VoxelShapes.union(full, south)
        if (state[EAST]) full = VoxelShapes.union(full, east)
        if (state[WEST]) full = VoxelShapes.union(full, west)

        return full
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? = createCodec { settings -> SignPostBlock(settings) }

    companion object {
        val UP: BooleanProperty = BooleanProperty.of("up")
        val DOWN: BooleanProperty = BooleanProperty.of("down")
        val NORTH: BooleanProperty = BooleanProperty.of("north")
        val SOUTH: BooleanProperty = BooleanProperty.of("south")
        val EAST: BooleanProperty = BooleanProperty.of("east")
        val WEST: BooleanProperty = BooleanProperty.of("west")
    }
}