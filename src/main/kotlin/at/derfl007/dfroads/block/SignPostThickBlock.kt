package at.derfl007.dfroads.block

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class SignPostThickBlock(settings: Settings) : SignPostBlock(settings) {


    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape? {
        val center = createCuboidShape(4.0, 4.0, 4.0, 12.0, 12.0, 12.0)
        val up = createCuboidShape(4.0, 12.0, 4.0, 12.0, 22.0, 12.0)
        val down = createCuboidShape(4.0, -4.0, 4.0, 12.0, 4.0, 12.0)
        val north = createCuboidShape(4.0, 4.0, -4.0, 12.0, 12.0, 4.0)
        val south = createCuboidShape(4.0, 4.0, 12.0, 12.0, 12.0, 22.0)
        val east = createCuboidShape(12.0, 4.0, 4.0, 22.0, 12.0, 12.0)
        val west = createCuboidShape(-4.0, 4.0, 4.0, 4.0, 12.0, 12.0)

        var full = center

        if (state[UP]) full = VoxelShapes.union(full, up)
        if (state[DOWN]) full = VoxelShapes.union(full, down)
        if (state[NORTH]) full = VoxelShapes.union(full, north)
        if (state[SOUTH]) full = VoxelShapes.union(full, south)
        if (state[EAST]) full = VoxelShapes.union(full, east)
        if (state[WEST]) full = VoxelShapes.union(full, west)

        return full
    }
    
}