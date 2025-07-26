package me.naburalis.batleroyalborder;

import net.fabricmc.api.ModInitializer;

import me.naburalis.batleroyalborder.commands.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatleRoyalBorder implements ModInitializer {
	public static final String MOD_ID = "batleroyalborder";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		if (!Config.exist()){
			Config.createConfig();
			Config.writeDefaultConfig();
		}

		Config.loadConfig();

		// Register custom commands
		SimpleRoyalCommand.register();
	}
}