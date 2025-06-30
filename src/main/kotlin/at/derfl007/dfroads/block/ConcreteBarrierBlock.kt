package at.derfl007.dfroads.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class ConcreteBarrierBlock(settings: Settings) : HorizontalFacingBlock(settings) {

    init {
        defaultState = defaultState.with(FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>?) {
        builder?.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return super.getPlacementState(ctx)?.with(FACING, ctx?.horizontalPlayerFacing?.opposite)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        return VoxelShapes.createHorizontalFacingShapeMap(
            createCuboidShape(4.0, 0.0, 0.0, 12.0, 12.75, 16.0),
        )[state[FACING]]
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? = createCodec(::ConcreteBarrierBlock)
}