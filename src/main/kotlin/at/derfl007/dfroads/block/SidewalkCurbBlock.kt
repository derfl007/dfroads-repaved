package at.derfl007.dfroads.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class SidewalkCurbBlock(settings: Settings) : StairLikeBlock(settings) {

    override fun getStraightShape(): VoxelShape {
        val curbShape = createCuboidShape(0.0, 16.0, 0.0, 16.0, 20.0, 4.0)
        return VoxelShapes.union(VoxelShapes.fullCube(), curbShape)
    }

    override fun getOuterShape(): VoxelShape {
        val curbShape1 = createCuboidShape(0.0, 16.0, 0.0, 16.0, 20.0, 4.0)
        val curbShape2 = createCuboidShape(12.0, 16.0, 0.0, 16.0, 20.0, 16.0)
        return VoxelShapes.union(VoxelShapes.fullCube(), curbShape1, curbShape2)
    }

    override fun getInnerShape(): VoxelShape {
        val curbShape = createCuboidShape(0.0, 16.0, 0.0, 4.0, 20.0, 4.0)
        return VoxelShapes.union(VoxelShapes.fullCube(), curbShape)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? =
        createCodec { settings -> SidewalkCurbBlock(settings) }

}