package com.github.anopensaucedev.methane_server.client;

import net.fabricmc.api.ClientModInitializer;

import static com.github.anopensaucedev.methane_server.Constants.MethaneServerLogger;

public class MethaneServerUtilsClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {

        MethaneServerLogger.warn("Methane Server Utils is installed on the Client! Please only keep this installed if you intend to use this for LAN.");
        MethaneServerLogger.warn("Methane is available at: https://modrinth.com/mod/methane");

    }
}
