package com.unascribed.drogtor.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.unascribed.drogtor.DrogtorPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

//I'm sorry.
@Mixin(PlayerListS2CPacket.class)
public class MixinPlayerListS2CPacket {
	private Map<UUID, String> drogtor$playerIdMap = new HashMap<>();

	@Inject(method = "<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;[Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("RETURN"))
	private void cachePlayerIdsArray(PlayerListS2CPacket.Action action, ServerPlayerEntity[] players, CallbackInfo info) {
		for (ServerPlayerEntity player : players) {
			if (player instanceof DrogtorPlayer && ((DrogtorPlayer) player).drogtor$isActive()) {
				drogtor$playerIdMap.put(player.getUuid(), ((DrogtorPlayer) player).drogtor$getNickname());
			}
		}
	}

	@Inject(method = "<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;Ljava/lang/Iterable;)V", at = @At("RETURN"))
	private void cachePlayerIdsIterable(PlayerListS2CPacket.Action action, Iterable<ServerPlayerEntity> players, CallbackInfo info) {
		for (ServerPlayerEntity player : players) {
			if (player instanceof DrogtorPlayer && ((DrogtorPlayer) player).drogtor$isActive()) {
				drogtor$playerIdMap.put(player.getUuid(), ((DrogtorPlayer) player).drogtor$getNickname());
			}
		}
	}

	/**
	 * @author b0undarybreaker
	 * @reason Replace the string ID in the sync packet with the Drogtor nickname
	 */
	@Redirect(method = "write", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getName()Ljava/lang/String;"))
	private String replacePlayerName(GameProfile profile) {
		return drogtor$playerIdMap.getOrDefault(profile.getId(), profile.getName());
	}
}
