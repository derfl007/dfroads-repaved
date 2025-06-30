package at.derfl007.dfroads.block

import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class LedSignBlock(settings: Settings) : RoadSignBlock(settings) {

    override fun getSignShape(minX: Double, minY: Double, maxX: Double, maxY: Double): VoxelShape =
        createCuboidShape(0.0, 0.0, 5.0, 16.0, 16.0, 11.0)
//
    override fun getPostShape(): VoxelShape = VoxelShapes.empty()
//
//    override fun isTransparent(state: BlockState?) = true
}