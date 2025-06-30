package at.derfl007.dfroads.block

import at.derfl007.dfroads.blockentity.ComplexRoadSignBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class ComplexRoadSignBlock(settings: Settings) : RoadSignBlock(settings) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ComplexRoadSignBlockEntity(pos, state)
}