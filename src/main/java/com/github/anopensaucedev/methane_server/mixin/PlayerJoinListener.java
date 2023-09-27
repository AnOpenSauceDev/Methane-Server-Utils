package com.github.anopensaucedev.methane_server.mixin;

import com.github.anopensaucedev.methane_server.Constants;
import com.github.anopensaucedev.methane_server.MethaneServerUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerJoinListener   {


    @Inject(method = "onPlayerConnect",at = @At("TAIL"))
    public void handleMethaneOnJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci){
        syncGameRules(player.getWorld());
        PacketByteBuf buf = PacketByteBufs.create();
        int[] intarraydata = {booltoInt(Constants.enforceModState),booltoInt(Constants.globalModState),booltoInt(Constants.forceMethane)}; // encode our data into an int array, because we cant write bool arrays.
        buf.writeIntArray(intarraydata);

        Constants.MethaneServerLogger.info("sending packet...");

        ServerPlayNetworking.send(player,Constants.METHANE_STATE_PACKET,buf);
    }

    // gamerule updates will only affect players AFTER rejoining.
    public void syncGameRules(World world){
        Constants.forceMethane = world.getGameRules().getBoolean(MethaneServerUtils.FORCE_METHANE);
        Constants.globalModState = world.getGameRules().getBoolean(MethaneServerUtils.GLOBAL_MOD_STATE);
        Constants.enforceModState = world.getGameRules().getBoolean(MethaneServerUtils.ENFORCE_MOD_STATE);
    }




    int booltoInt(boolean bool){
        return bool ? 1 : 0; // true = 1
    }

}
