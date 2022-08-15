package com.unascribed.drogtor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

	@Shadow
	public ServerPlayerEntity player;
	
	@Redirect(at=@At(value="INVOKE", target="Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"),
			method="onDisconnected(Lnet/minecraft/text/Text;)V")
	public void broadcast(PlayerManager subject, Text msg, boolean overlay, Text reason) {
		if (msg.getContent() instanceof TranslatableTextContent ttc) {
			String key = ttc.getKey();
			if ("multiplayer.player.left".equals(key)) {
				subject.sendToAll(new GameMessageS2CPacket(msg, overlay));
				subject.getServer().sendMessage(Text.translatable("multiplayer.player.left", player.getGameProfile().getName()));
				return;
			}
		}
		subject.broadcast(msg, overlay);
	}
	
}
