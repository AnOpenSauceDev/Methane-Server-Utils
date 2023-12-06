package com.github.anopensaucedev.methane_server.mixin;

import com.github.anopensaucedev.methane_server.Constants;
import com.github.anopensaucedev.methane_server.MethaneServerUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.List;

import static com.github.anopensaucedev.methane_server.MethaneServerUtils.hasMethane;

@Mixin(value = PlayerManager.class, priority = 2000) // give a higher priority (might fix things, might break things.)
public abstract class PlayerJoinListener   {


    @Shadow private int latencyUpdateTimer;

    @Shadow public abstract void sendToAll(Packet<?> packet);

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow public abstract void remove(ServerPlayerEntity player);

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect",at = @At("TAIL"))
    public void handleMethaneOnJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci){
        hasMethane.put(player,Boolean.FALSE);
        syncGameRules(player.getWorld());
        PacketByteBuf buf = PacketByteBufs.create();
        int[] intarraydata = {booltoInt(Constants.enforceModState),booltoInt(Constants.globalModState),booltoInt(Constants.forceMethane)}; // encode our data into an int array, because we cant write bool arrays.
        buf.writeIntArray(intarraydata);
        // we can't send packets just yet, otherwise we'll run into a nasty race condition

        SendPacket(player,buf);


    }

    void SendPacket(ServerPlayerEntity player, PacketByteBuf buf){
        
        ServerPlayNetworking.send(player, Constants.METHANE_STATE_PACKET,buf);
    }

    // gamerule updates will only affect players AFTER rejoining.
    public void syncGameRules(World world){
        Constants.forceMethane = world.getGameRules().getBoolean(MethaneServerUtils.FORCE_METHANE);
        Constants.globalModState = world.getGameRules().getBoolean(MethaneServerUtils.GLOBAL_MOD_STATE);
        Constants.enforceModState = world.getGameRules().getBoolean(MethaneServerUtils.ENFORCE_MOD_STATE);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updatePlayerLatency() {
        if (++latencyUpdateTimer > 600) { // will ban all non-methane players every 30 seconds, a bit of a hack
            sendToAll(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LATENCY), players));
            for(int i = 0; i < players.size(); i++){
                if(!hasMethane.get(players.get(i))){
                    if(players.get(i).getWorld().getGameRules().getBoolean(MethaneServerUtils.FORCE_METHANE)){
                      players.get(i).networkHandler.disconnect(Text.of("This server requires Methane to play on. Get Methane at: https://modrinth.com/mod/methane"));
                    }
                }
            }
            this.latencyUpdateTimer = 0;
        }
    }




    int booltoInt(boolean bool){
        return bool ? 1 : 0; // true = 1
    }

}
