package com.unascribed.drogtor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DrogtorMod implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedi) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("nick")
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("nick", StringArgumentType.greedyString())
							.executes((c) -> {
								// replace § just in case
								Text oldDn = c.getSource().getPlayer().getDisplayName();
								String newDn = c.getArgument("nick", String.class).replace("§", "");
								((DrogtorPlayer) c.getSource().getPlayer()).drogtor$setNickname(newDn);
								informDisplayName(c.getSource().getPlayer(), oldDn);
								if (newDn.length() <= 16) {
									((DrogtorPlayer) c.getSource().getPlayer()).drogtor$setNamecard(newDn);
									informNamecard(c.getSource().getPlayer());
								} else {
									c.getSource().sendFeedback(new LiteralText("Your display name is longer than 16 characters! Please use `/namecard` to set your namecard due to vanilla limitations").formatted(Formatting.YELLOW), false);
								}
								return 1;
							}))
					.executes((c) -> {
						Text oldDn = c.getSource().getPlayer().getDisplayName();
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNickname(null);
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNamecard(null);
						informDisplayName(c.getSource().getPlayer(), oldDn);
						informNamecard(c.getSource().getPlayer());
						return 1;
					}));
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("color")
					.then(RequiredArgumentBuilder.<ServerCommandSource, Formatting>argument("color", ColorArgumentType.color())
							.executes((c) -> {
								Formatting f = c.getArgument("color", Formatting.class);
								if (f == Formatting.BLACK) throw ColorArgumentType.INVALID_COLOR_EXCEPTION.create("Black is not a permitted name color");
								((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNameColor(f);
								informDisplayName(c.getSource().getPlayer(), null);
								return 1;
							}))
					.executes((c) -> {
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNameColor(null);
						informDisplayName(c.getSource().getPlayer(), null);
						return 1;
					}));
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("namecard")
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("namecard", StringArgumentType.greedyString())
							.executes((c) -> {
								// replace § just in case
								String namecard = c.getArgument("namecard", String.class).replace("§", "");
								if (namecard.length() > 16) {
									c.getSource().sendError(new LiteralText("Namecard must be 16 or fewer characters due to vanilla limitations"));
									return 0;
								}
								((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNamecard(namecard);
								informNamecard(c.getSource().getPlayer());
								return 1;
							}))
					.executes((c) -> {
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNamecard(null);
						informNamecard(c.getSource().getPlayer());
						return 1;
					}));
		});
	}

	private void informDisplayName(ServerPlayerEntity player, Text oldDn) {
		player.sendMessage(new LiteralText("Your display name is now ").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)).append(player.getDisplayName()), false);
		if (oldDn != null) {
			Text t = oldDn.shallowCopy().append(new LiteralText(" is now known as ").setStyle(Style.EMPTY.withColor(Formatting.YELLOW))).append(player.getDisplayName());
			for (ServerPlayerEntity spe : player.server.getPlayerManager().getPlayerList()) {
				if (spe != player) spe.sendMessage(t, false);
			}
		}
	}

	private void informNamecard(ServerPlayerEntity player) {
		player.sendMessage(new LiteralText("Your namecard is now ").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)).append(((DrogtorPlayer)player).drogtor$getNamecard()), false);
	}
}
