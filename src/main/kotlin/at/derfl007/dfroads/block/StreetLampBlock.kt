package at.derfl007.dfroads.block

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class StreetLampBlock(settings: Settings) : SignPostBlock(settings) {

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        val shape = createCuboidShape(4.5, 6.5, -2.0, 11.5, 9.0, 6.5)
        val shapeMap = VoxelShapes.createHorizontalFacingShapeMap(shape)
        return VoxelShapes.union(
            super.getOutlineShape(state, world, pos, context),
            shapeMap[state[FACING]]
        )
    }
}