package at.derfl007.dfroads.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class TrafficConeBlock(settings: Settings) : Block(settings) {

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape? {
        return createCuboidShape(3.0, 0.0, 3.0, 13.0, 10.0, 13.0)
    }
}