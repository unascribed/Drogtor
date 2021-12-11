package com.unascribed.drogtor;

import java.util.regex.Pattern;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DrogtorMod implements ModInitializer {
	private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\\\.");
	
	@Override
	public void onInitialize() {
		CommandRegistration.register((dispatcher, dedi) -> {
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("nick")
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("nick", StringArgumentType.greedyString())
							.executes((c) -> {
								// replace § just in case
								Text oldDn = c.getSource().getPlayer().getDisplayName();
								String newDn = c.getArgument("nick", String.class).replace("§", "");
								((DrogtorPlayer) c.getSource().getPlayer()).drogtor$setNickname(newDn);
								informDisplayName(c.getSource().getPlayer(), oldDn);
								return 1;
							}))
					.executes((c) -> {
						Text oldDn = c.getSource().getPlayer().getDisplayName();
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setNickname(null);
						informDisplayName(c.getSource().getPlayer(), oldDn);
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
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("bio")
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("bio", StringArgumentType.greedyString())
							.executes((c) -> {
								// replace § just in case
								String bio = c.getArgument("bio", String.class).replace("§", "");
								bio = ESCAPE_PATTERN.matcher(bio).replaceAll(res -> {
									String s = res.group().substring(1);
									if (s.equals("n")) {
										return "\n";
									} else if (s.equals("\\")) {
										return "\\\\";
									}
									return s;
								});
								((DrogtorPlayer) c.getSource().getPlayer()).drogtor$setBio(bio);
								informBio(c.getSource().getPlayer());
								return 1;
							}))
					.executes((c) -> {
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setBio(null);
						informBio(c.getSource().getPlayer());
						return 1;
					}));
			dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("bio")
					.then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("bio", StringArgumentType.greedyString())
							.executes((c) -> {
								// replace § just in case
								String bio = c.getArgument("bio", String.class).replace("§", "");
								bio = ESCAPE_PATTERN.matcher(bio).replaceAll(res -> {
									String s = res.group().substring(1);
									if (s.equals("n")) {
										return "\n";
									} else if (s.equals("\\")) {
										return "\\\\";
									}
									return s;
								});
								((DrogtorPlayer) c.getSource().getPlayer()).drogtor$setBio(bio);
								informBio(c.getSource().getPlayer());
								return 1;
							}))
					.executes((c) -> {
						((DrogtorPlayer)c.getSource().getPlayer()).drogtor$setBio(null);
						informBio(c.getSource().getPlayer());
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

	private void informBio(ServerPlayerEntity player) {
		String bio = ((DrogtorPlayer)player).drogtor$getBio();
		if (bio != null) {
			player.sendMessage(new LiteralText("Your bio is now:\n").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)).append(bio), false);
		}
	}

}
