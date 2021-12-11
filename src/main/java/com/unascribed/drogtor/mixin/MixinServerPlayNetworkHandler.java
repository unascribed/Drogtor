package com.unascribed.drogtor.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

	@Shadow
	public ServerPlayerEntity player;
	
	@Redirect(at=@At(value="INVOKE", target="net/minecraft/server/PlayerManager.broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"),
			method="onDisconnected(Lnet/minecraft/text/Text;)V")
	public void broadcast(PlayerManager subject, Text msg, MessageType type, UUID senderUuid) {
		if (msg instanceof TranslatableText) {
			TranslatableText tt;
			tt = ((TranslatableText)msg);
			String key = tt.getKey();
			if ("multiplayer.player.left".equals(key)) {
				subject.sendToAll(new GameMessageS2CPacket(msg, type, senderUuid));
				subject.getServer().sendSystemMessage(new TranslatableText("multiplayer.player.left", player.getGameProfile().getName()), senderUuid);
				return;
			}
		}
		subject.broadcast(msg, type, senderUuid);
	}
	
}
