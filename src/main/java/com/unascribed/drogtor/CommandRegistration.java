package com.unascribed.drogtor;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistration {

	public static void register(TriConsumer<CommandDispatcher<ServerCommandSource>, CommandRegistryAccess, CommandManager.RegistrationEnvironment> callback) {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> callback.accept(dispatcher, registryAccess, environment));
	}
	
}
