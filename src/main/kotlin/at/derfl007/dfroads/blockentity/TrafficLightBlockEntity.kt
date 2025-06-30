package at.derfl007.dfroads.blockentity

import at.derfl007.dfroads.block.RedstoneTransmitterBlock
import at.derfl007.dfroads.registry.BlockEntityRegistry.TRAFFIC_LIGHT_BLOCK_ENTITY
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

/**
 * Block entity for [at.derfl007.dfroads.block.TrafficLightBlock]
 */
class TrafficLightBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(TRAFFIC_LIGHT_BLOCK_ENTITY, pos, state) {
    var state: TrafficLightState = TrafficLightState.ON
        set(value) {
            field = value
            markDirty()
        }

    var phases: List<TrafficLightPhase> = TrafficLightPhase.Companion.DEFAULT_PHASES
        set(value) {
            field = value
            // reset currentPhaseIndex and timeUntilNextPhase if the new array is smaller
            if (currentPhaseIndex >= field.size) {
                currentPhaseIndex = 0
                pulsesToNextPhase = 0
            }
            markDirty()
        }

    var currentPhaseIndex: Int = 0

    var pulsesToNextPhase: Int = 0
        set(value) {
            field = value
            markDirty()
        }

    var lastRedstonePower: Int = 0

    fun getCurrentPhase(): TrafficLightPhase? {
        return if (phases.isNotEmpty() && currentPhaseIndex < phases.size) phases[currentPhaseIndex] else null
    }

    fun nextPhase() {
        currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
    }

    override fun writeNbt(nbt: NbtCompound, registries: RegistryWrapper.WrapperLookup?) {
        nbt.putInt("state", state.ordinal)
        nbt.putIntArray("phases", phases.map { it.mapToInt() }.toIntArray())
        nbt.putInt("timeUntilNextPhase", pulsesToNextPhase)
        nbt.putInt("currentPhase", currentPhaseIndex)
        super.writeNbt(nbt, registries)
    }

    override fun readNbt(nbt: NbtCompound, registries: RegistryWrapper.WrapperLookup?) {
        super.readNbt(nbt, registries)
        state = TrafficLightState.entries[nbt.getInt("state").getOrDefault(state.ordinal)]
        phases =
            nbt.getIntArray("phases").getOrNull()?.map { TrafficLightPhase.mapFromInt(it) }?.toList() ?: phases
        pulsesToNextPhase = nbt.getInt("timeUntilNextPhase").getOrDefault(pulsesToNextPhase)
        currentPhaseIndex = nbt.getInt("currentPhase").getOrDefault(currentPhaseIndex)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener?>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup?): NbtCompound? = createNbt(registries)

    companion object {
        val TrafficLightBlockEntityTicker = BlockEntityTicker<TrafficLightBlockEntity> { world, pos, state, entity ->
            val currentPower = state[RedstoneTransmitterBlock.Companion.POWER]
            if (currentPower > entity.lastRedstonePower) {
                entity.pulsesToNextPhase++
            }

            entity.lastRedstonePower = currentPower

            if (entity.currentPhaseIndex >= entity.phases.size) {
                // something changed, reset
                entity.currentPhaseIndex = 0
                entity.pulsesToNextPhase = 0
                return@BlockEntityTicker
            }
            if (entity.pulsesToNextPhase >= entity.phases[entity.currentPhaseIndex].time) {
                entity.nextPhase()
                entity.pulsesToNextPhase = 0
            }
        }
    }

    enum class TrafficLightState {
        ON, WARN, OFF
    }

    data class TrafficLightPhase(
        var isRedOn: Boolean = false,
        var isYellowOn: Boolean = false,
        var isGreenOn: Boolean = false,
        var time: Int = 0
    ) {
        fun mapToInt(): Int {
            var bitmap = 0
            if (isRedOn) bitmap = bitmap or 0b1
            if (isYellowOn) bitmap = bitmap or 0b10
            if (isGreenOn) bitmap = bitmap or 0b100
            val timeShifted = time shl 3
            return bitmap or timeShifted
        }

        companion object {
            fun mapFromInt(bitmap: Int): TrafficLightPhase {
                val isRedOn = bitmap and 0b1 != 0
                val isYellowOn = bitmap and 0b10 != 0
                val isGreenOn = bitmap and 0b100 != 0
                val timeShifted = bitmap shr 3
                return TrafficLightPhase(isRedOn, isYellowOn, isGreenOn, timeShifted)
            }

            fun greenPhase(time: Int) = TrafficLightPhase(isGreenOn = true, time = time)
            fun yellowPhase(time: Int) = TrafficLightPhase(isYellowOn = true, time = time)
            fun redPhase(time: Int) = TrafficLightPhase(isRedOn = true, time = time)
            fun redYellowPhase(time: Int) = TrafficLightPhase(isRedOn = true, isYellowOn = true, time = time)
            fun offPhase(time: Int) = TrafficLightPhase(time = time)

            val DEFAULT_PHASES = listOf(
                redPhase(14),
                redYellowPhase(1),
                greenPhase(8),
                offPhase(1),
                greenPhase(1),
                offPhase(1),
                greenPhase(1),
                offPhase(1),
                greenPhase(1),
                yellowPhase(1),
            )
        }
    }
}