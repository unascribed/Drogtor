package com.unascribed.drogtor;

import java.util.function.BiConsumer;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistration {

	public static void register(BiConsumer<CommandDispatcher<ServerCommandSource>, Boolean> callback) {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedi) -> callback.accept(dispatcher, dedi));
	}
	
}
