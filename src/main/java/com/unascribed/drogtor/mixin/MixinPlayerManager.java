package com.unascribed.drogtor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

	@Redirect(at=@At(value="INVOKE", target="Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"),
			method="onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void broadcast(PlayerManager subject, Text msg, boolean overlay, ClientConnection connection, ServerPlayerEntity player) {
		if (msg.getContent() instanceof TranslatableTextContent ttc) {
			String key = ttc.getKey();
			if ("multiplayer.player.joined".equals(key)) {
				subject.sendToAll(new GameMessageS2CPacket(msg, overlay));
				subject.getServer().sendMessage(Text.translatable("multiplayer.player.joined", player.getGameProfile().getName()));
				return;
			} else if ("multiplayer.player.joined.renamed".equals(key)) {
				subject.sendToAll(new GameMessageS2CPacket(msg, overlay));
				subject.getServer().sendMessage(Text.translatable("multiplayer.player.joined.renamed", player.getGameProfile().getName(), ttc.getArgs()[1]));
				return;
			}
		}
		subject.broadcast(msg, overlay);
	}
	
}
