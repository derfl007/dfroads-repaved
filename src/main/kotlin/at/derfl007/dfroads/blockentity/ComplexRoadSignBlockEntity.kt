package at.derfl007.dfroads.blockentity

import at.derfl007.dfroads.registry.BlockEntityRegistry
import at.derfl007.dfroads.util.Color
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import kotlin.jvm.optionals.getOrDefault

class ComplexRoadSignBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(
    BlockEntityRegistry.COMPLEX_ROAD_SIGN_BLOCK_ENTITY,
    pos,
    state
) {

    var backgroundTexture: Int = 0
        set(value) {
            field = value
            markDirty()
        }

    var height: Float = 1f
        set(value) {
            field = value
            markDirty()
        }
    var width: Float = 1f
        set(value) {
            field = value
            markDirty()
        }

    var elements: List<SignElement> = emptyList()
        set(value) {
            field = value
            markDirty()
        }

    override fun writeNbt(nbt: NbtCompound, registries: RegistryWrapper.WrapperLookup) {
        super.writeNbt(nbt, registries)
        nbt.putInt("backgroundTexture", backgroundTexture)
        nbt.putFloat("height", height)
        nbt.putFloat("width", width)
        nbt.copyFromCodec(SignElement.CODEC.listOf().fieldOf("elements"), elements)
    }

    override fun readNbt(nbt: NbtCompound, registries: RegistryWrapper.WrapperLookup) {
        backgroundTexture = nbt.getInt("backgroundTexture").getOrDefault(0)
        height = nbt.getFloat("height").getOrDefault(0f)
        width = nbt.getFloat("width").getOrDefault(0f)
        elements = nbt.decode(SignElement.CODEC.listOf().fieldOf("elements")).getOrDefault(emptyList())
        super.readNbt(nbt, registries)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener?>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup?): NbtCompound? = createNbt(registries)


    companion object {
        const val MAX_WIDTH = 7f
        const val MAX_HEIGHT = 5f
    }

    /**
     * @param type The type of element. Can be text, arrow, icon or container
     * @param color The hex code for the element's color (or background color for containers)
     * @param borderColor The hex code for the element's border color (-1 to disable border)
     * @param text Depending on the type, can be text, icon type or arrow type
     */
    data class SignElement(
        var x: Float = 0f,
        var y: Float = 0f,
        var width: Float = 0f,
        var height: Float = 0f,
        var type: Type = Type.TEXT,
        var color: Color = Color.NONE,
        var borderColor: Color = Color.NONE,
        var text: String = "",
        var iconTexture: Int = 0,
        var rotation: Int = 0
    ) {
        enum class Type : StringIdentifiable {
            TEXT,
            ARROW,
            ICON,
            BOX;

            override fun asString(): String? = toString()

            companion object {
                val CODEC: StringIdentifiable.EnumCodec<Type> = StringIdentifiable.createCodec(Type::values);
            }
        }

        companion object {
            val CODEC: Codec<SignElement> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(SignElement::x),
                    Codec.FLOAT.fieldOf("y").forGetter(SignElement::y),
                    Codec.FLOAT.fieldOf("width").forGetter(SignElement::width),
                    Codec.FLOAT.fieldOf("height").forGetter(SignElement::height),
                    Type.CODEC.fieldOf("type").forGetter(SignElement::type),
                    Color.CODEC.fieldOf("color").forGetter(SignElement::color),
                    Color.CODEC.fieldOf("borderColor").forGetter(SignElement::borderColor),
                    Codec.STRING.fieldOf("text").forGetter(SignElement::text),
                    Codec.INT.fieldOf("iconTexture").forGetter(SignElement::iconTexture),
                    Codec.INT.fieldOf("rotation").forGetter(SignElement::rotation),
                ).apply(instance, ::SignElement)
            }
        }
    }
}