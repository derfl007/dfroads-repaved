package at.derfl007.dfroads.blockentity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType

object Utils {
    @Suppress("UNCHECKED_CAST")
    fun <E : BlockEntity, A : BlockEntity> validateTicker(
        givenType: BlockEntityType<A>,
        expectedType: BlockEntityType<E>,
        ticker: BlockEntityTicker<in E>
    ): BlockEntityTicker<A>? {
        return (if (expectedType === givenType) ticker else null) as BlockEntityTicker<A>?
    }
}