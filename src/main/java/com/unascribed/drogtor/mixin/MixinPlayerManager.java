package com.unascribed.drogtor.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

	@Redirect(at=@At(value="INVOKE", target="net/minecraft/server/PlayerManager.broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"),
			method="onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void broadcast(PlayerManager subject, Text msg, MessageType type, UUID senderUuid, ClientConnection conn, ServerPlayerEntity player) {
		if (msg instanceof TranslatableText) {
			TranslatableText tt;
			tt = ((TranslatableText)msg);
			String key = tt.getKey();
			if ("multiplayer.player.joined".equals(key)) {
				subject.sendToAll(new GameMessageS2CPacket(msg, type, senderUuid));
				subject.getServer().sendSystemMessage(new TranslatableText("multiplayer.player.joined", player.getGameProfile().getName()), senderUuid);
				return;
			} else if ("multiplayer.player.joined.renamed".equals(key)) {
				subject.sendToAll(new GameMessageS2CPacket(msg, type, senderUuid));
				subject.getServer().sendSystemMessage(new TranslatableText("multiplayer.player.joined.renamed", player.getGameProfile().getName(), tt.getArgs()[1]), senderUuid);
				return;
			}
		}
		subject.broadcast(msg, type, senderUuid);
	}
	
}
