package at.derfl007.dfroads

import at.derfl007.dfroads.registry.*
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Suppress("UNUSED")
object DFRoads : ModInitializer {
    const val SCHEMA_VERSION: Int = 2
    const val MOD_ID = "dfroads"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID);


    // Note: Order matters!
    override fun onInitialize() {
        LOGGER.info("Initializing {}", MOD_ID)

        ComponentRegistry.registerComponents()
        ItemRegistry.registerItems()
        ItemRegistry.registerItemGroups()
        BlockRegistry.registerBlocks()
        BlockEntityRegistry.registerBlockEntities()
        ServerNetworkingRegistry.registerPayloads()
        ServerNetworkingRegistry.registerServerReceivers()

        LOGGER.info("{} initialized. Have fun <3", MOD_ID)
    }

    fun id(path: String): Identifier = Identifier.of(MOD_ID, path)
}