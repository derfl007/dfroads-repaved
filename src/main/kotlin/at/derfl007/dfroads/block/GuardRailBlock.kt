package at.derfl007.dfroads.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class GuardRailBlock(settings: Settings) : StairLikeBlock(settings) {

    override fun getStraightShape(): VoxelShape {
        val postShape = createCuboidShape(7.0, 0.0, 2.0, 9.0, 15.0, 4.0)
        val railShape = createCuboidShape(0.0, 6.0, 0.0, 16.0, 14.0, 3.0)
        return VoxelShapes.union(postShape, railShape)
    }

    override fun getOuterShape(): VoxelShape {
        val postShape = createCuboidShape(12.0, 0.0, 2.0, 14.0, 15.0, 4.0)
        val railShape1 = createCuboidShape(0.0, 6.0, 0.0, 16.0, 14.0, 3.0)
        val railShape2 = createCuboidShape(13.0, 6.0, 0.0, 16.0, 14.0, 16.0)
        return VoxelShapes.union(postShape, railShape1, railShape2)
    }

    override fun getInnerShape(): VoxelShape {
        return createCuboidShape(0.0, 0.0, 0.0, 2.0, 15.0, 2.0)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock?>? =
        createCodec { settings -> GuardRailBlock(settings) }

}