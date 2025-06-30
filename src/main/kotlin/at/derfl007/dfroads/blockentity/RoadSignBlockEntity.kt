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
import net.minecraft.util.math.BlockPos

class RoadSignBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockEntityRegistry.ROAD_SIGN_BLOCK_ENTITY, pos, state) {

    var texture: Int = 0
        set(value) {
            field = value
            markDirty()
        }

    var size: Int = 0
        set(value) {
            field = value
            markDirty()
        }

    // Can be anything from town names on town signs, to the numbers on speed limit signs
//    var customText: String = ""
//        set(value) {
//            field = value
//            markDirty()
//        }

    override fun writeNbt(nbt: NbtCompound, registries: RegistryWrapper.WrapperLookup) {
        nbt.putInt("texture", texture)
        nbt.putInt("size", size)
//        nbt.putString("customText", customText)

        super.writeNbt(nbt, registries)
    }

    override fun readNbt(nbt: NbtCompound, registries: RegistryWrapper.WrapperLookup) {
        super.readNbt(nbt, registries)
        texture = nbt.getInt("texture").get()
        size = (if (cachedState.block !is LedSignBlock) nbt.getInt("size").get() else 1)
//        customText = nbt.getString("customText").get()
    }

// TODO: 1.21.6 stuff, keep here until update
//    override fun writeData(view: WriteView) {
//        view.putInt("size", size)
//        view.putInt("texture", texture)
//        view.putString("customText", customText)
//
//        super.writeData(view)
//    }
//
//    override fun readData(view: ReadView) {
//        super.readData(view)
//        size = view.getInt("size", 0)
//        texture = view.getInt("texture", 0)
//        customText = view.getString("customText", "")
//    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener?>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup): NbtCompound? = createNbt(registries)
}