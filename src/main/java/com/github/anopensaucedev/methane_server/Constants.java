package com.github.anopensaucedev.methane_server;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Constants {

    public static final String ModID = "methane_server";
    public static final String FRIENDLY_NAME = "Methane Server Utilities";

    public static final Identifier METHANE_STATE_PACKET = new Identifier(ModID,"statepacket");

    public static Logger MethaneServerLogger = LoggerFactory.getLogger(FRIENDLY_NAME); // instead of using ModID, we use a nice looking name so logs are easier to understand for the end user.

    public static boolean enforceModState; // enforce whether the mod state should be forced to what globalModState is.

    public static boolean globalModState; // if enforceModState is false, the user will be "recommended" to use the state, but the client will not have to change.

    public static boolean forceMethane; // If the user doesn't have methane installed, disconnect them and tell them to install it. Useful for preventing people from using other client-only fullbright mods (Sorry GammaUtils).



}
