package at.derfl007.dfroads.blockentity

import at.derfl007.dfroads.block.LedSignBlock
import at.derfl007.dfroads.registry.BlockEntityRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.math.BlockPos

class RoadSignBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockEntityRegistry.ROAD_SIGN_BLOCK_ENTITY, pos, state) {

    var texture: String = "sign_speed_10"
        set(value) {
            field = value
            markDirty()
        }

    var size: Int = 0
        set(value) {
            field = value
            markDirty()
        }

    override fun writeData(view: WriteView) {
        view.putString("texture", texture)
        view.putInt("size", size)
        super.writeData(view)
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        texture = view.getString("texture", "sign_speed_10")
        size = (if (cachedState.block !is LedSignBlock) view.getInt("size", 1) else 1)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener?>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound? = createNbt(registries)
}