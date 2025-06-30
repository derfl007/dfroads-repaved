package at.derfl007.dfroads.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.block.enums.StairShape
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
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
 * Mimics the stair behavior of having an inner, outer and straight state, but without the top/bottom stuff (lol)
 */
abstract class StairLikeBlock(settings: Settings) : HorizontalFacingBlock(settings) {

    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.NORTH).with(SHAPE, StairShape.STRAIGHT)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val state = super.getPlacementState(ctx)
            ?.with(FACING, ctx.horizontalPlayerFacing)

        return state?.with(SHAPE, getShape(state, ctx.world, ctx.blockPos))
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, SHAPE)
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
    ): BlockState {
        return if (direction.axis.isHorizontal)
            state.with(SHAPE, getShape(state, world, pos))
        else
            super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random)

    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        val direction = state[FACING]
        val shape = state[SHAPE]

        when (mirror) {
            BlockMirror.LEFT_RIGHT -> {
                if (direction.axis == Direction.Axis.Z) {
                    return when (shape) {
                        StairShape.OUTER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.OUTER_RIGHT)

                        StairShape.INNER_RIGHT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.INNER_LEFT)

                        StairShape.INNER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.INNER_RIGHT)

                        StairShape.OUTER_RIGHT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.OUTER_LEFT)

                        StairShape.STRAIGHT -> state.rotate(BlockRotation.CLOCKWISE_180)
                    }
                }
            }

            BlockMirror.FRONT_BACK -> {
                if (direction.axis == Direction.Axis.X) {
                    return when (shape) {
                        StairShape.OUTER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.OUTER_RIGHT)

                        StairShape.INNER_RIGHT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.INNER_RIGHT)

                        StairShape.INNER_LEFT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.INNER_LEFT)

                        StairShape.OUTER_RIGHT -> state.rotate(BlockRotation.CLOCKWISE_180)
                            .with(SHAPE, StairShape.OUTER_LEFT)

                        StairShape.STRAIGHT -> state.rotate(BlockRotation.CLOCKWISE_180)
                    }
                }
            }

            else -> return super.mirror(state, mirror)
        }
        return super.mirror(state, mirror)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        val direction = state[FACING].opposite

        val shapeMap = when (state[SHAPE]) {
            StairShape.STRAIGHT -> VoxelShapes.createHorizontalFacingShapeMap(getStraightShape())
            StairShape.INNER_LEFT, StairShape.INNER_RIGHT -> VoxelShapes.createHorizontalFacingShapeMap(getInnerShape())
            StairShape.OUTER_LEFT, StairShape.OUTER_RIGHT -> VoxelShapes.createHorizontalFacingShapeMap(getOuterShape())
        }
        return shapeMap[when (state[SHAPE]) {
            StairShape.STRAIGHT, StairShape.INNER_LEFT, StairShape.OUTER_RIGHT -> direction
            StairShape.OUTER_LEFT -> direction.rotateYCounterclockwise()
            StairShape.INNER_RIGHT -> direction.rotateYCounterclockwise()
        }]
    }

    abstract fun getStraightShape(): VoxelShape
    abstract fun getOuterShape(): VoxelShape
    abstract fun getInnerShape(): VoxelShape

    fun getShape(state: BlockState, world: BlockView, pos: BlockPos): StairShape {
        val direction = state[FACING]
        val nextBlockState = world.getBlockState(pos.offset(direction))
        if (nextBlockState.block is StairLikeBlock) {
            val nextBlockDirection = nextBlockState[FACING]
            if (nextBlockDirection.axis != state[FACING].axis
                && isDifferentOrientation(state, world, pos, nextBlockDirection.opposite)
            ) {
                return if (nextBlockDirection == direction.rotateYCounterclockwise()) StairShape.OUTER_LEFT else StairShape.OUTER_RIGHT
            }
        }

        val previousBlockState = world.getBlockState(pos.offset(direction.opposite))
        if (previousBlockState.block is StairLikeBlock) {
            val previousBlockDirection = previousBlockState[FACING]
            if (previousBlockDirection.axis != state[FACING].axis
                && isDifferentOrientation(state, world, pos, previousBlockDirection)
            ) {
                return if (previousBlockDirection == direction.rotateYCounterclockwise()) StairShape.INNER_LEFT else StairShape.INNER_RIGHT
            }
        }
        return StairShape.STRAIGHT
    }

    companion object {
        val SHAPE: EnumProperty<StairShape> = EnumProperty.of("shape", StairShape::class.java)

        private fun isDifferentOrientation(
            state: BlockState,
            world: BlockView,
            pos: BlockPos,
            dir: Direction?
        ): Boolean {
            val blockState = world.getBlockState(pos.offset(dir))
            return blockState.block !is StairLikeBlock || blockState[FACING] != state[FACING]
        }
    }
}