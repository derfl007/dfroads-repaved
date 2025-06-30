package at.derfl007.dfroads

import at.derfl007.dfroads.registry.ClientNetworkingRegistry
import at.derfl007.dfroads.registry.ModelRegistry
import at.derfl007.dfroads.registry.RendererRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment


@Environment(EnvType.CLIENT)
object DFRoadsClient : ClientModInitializer {

    override fun onInitializeClient() {
        DFRoads.LOGGER.info("{} Client initializing", DFRoads.MOD_ID)

        ModelRegistry.registerModels()
        RendererRegistry.registerRenderers()
        ClientNetworkingRegistry.registerReceivers()

        DFRoads.LOGGER.info("{} Client initialized", DFRoads.MOD_ID)
    }
}