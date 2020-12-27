package com.unascribed.drogtor;

import java.util.function.BiConsumer;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

public class CommandRegistration {

	public static void register(BiConsumer<CommandDispatcher<CommandSource>, Boolean> callback) {
		MinecraftForge.EVENT_BUS.addListener((FMLServerAboutToStartEvent e) -> {
			callback.accept(e.getServer().getCommandManager().getDispatcher(), e.getServer().isDedicatedServer());
		});
	}
	
}
