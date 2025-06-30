package at.derfl007.dfroads.block

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class PedestrianTrafficLightBlock(settings: Settings) : TrafficLightBlock(settings) {

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return createCuboidShape(4.0, 0.0, 4.0, 12.0, 11.0, 12.0)
    }
}