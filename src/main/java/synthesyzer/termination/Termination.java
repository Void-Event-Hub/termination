package synthesyzer.termination;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import synthesyzer.termination.command.TMCommands;
import synthesyzer.termination.config.MyConfig;
import synthesyzer.termination.events.TMEvents;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.registry.blocks.TMBlockEntities;
import synthesyzer.termination.registry.blocks.TMBlocks;

public class Termination implements ModInitializer {
    public static final String MOD_ID = "termination";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final MyConfig CONFIG = MyConfig.createAndLoad();

    @Override
    public void onInitialize() {
        TMBlocks.register();
        TMBlockEntities.register();
        TMCommands.register();
        TMEvents.register();

        TMNetwork.register();
        LOGGER.info("Hello Fabric world!");
    }
}