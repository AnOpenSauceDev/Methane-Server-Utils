package com.github.anopensaucedev.methane_server;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.HashMap;

public class MethaneServerUtils implements ModInitializer {



    // default settings for our gamerules.
    public static final GameRules.Key<GameRules.BooleanRule> ENFORCE_MOD_STATE = GameRuleRegistry.register("enforceMethaneState", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> GLOBAL_MOD_STATE = GameRuleRegistry.register("defaultMethaneState", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> FORCE_METHANE = GameRuleRegistry.register("forceMethane", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false)); // allow other fullbright mods like GammaUtils to work (although we can't control it).

    public static final Identifier METHANE_RESP_PACKET = new Identifier("methane_server","pong");

    public static HashMap<PlayerEntity,Boolean> hasMethane = new HashMap<>();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        ServerPlayNetworking.registerGlobalReceiver(METHANE_RESP_PACKET, ((server, player, handler, buf, responseSender) -> {
            hasMethane.put(player,Boolean.TRUE);
        }));

        Constants.MethaneServerLogger.info("Methane Server Utils has started!");

    }
}
