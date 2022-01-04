package com.unascribed.drogtor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

@Mod(modid="drogtor", name="Drogtor", version="1.0", useMetadata=true)
public class Drogtor {

	@Mod.PreInit
	public void onPreInit(FMLPreInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Mod.ServerStarting
	public void onServerStarting(FMLServerStartingEvent e) {
		e.registerServerCommand(new CommandNick());
		e.registerServerCommand(new CommandColor());
	}
	
	@ForgeSubscribe
	public void onChat(ServerChatEvent e) {
		if (e.player.getEntityData().hasKey("drogtor:nick") || e.player.getEntityData().hasKey("drogtor:color")) {
			String nick = getColoredNick(e.player);
			String needle = "<"+e.username+">";
			Pattern p = Pattern.compile(needle, Pattern.LITERAL);
			e.line = p.matcher(e.line).replaceFirst(Matcher.quoteReplacement("<"+nick+"§r>"));
		}
	}

	public static String getColoredNick(EntityPlayer p) {
		String nick = p.getCommandSenderName();
		if (p.getEntityData().hasKey("drogtor:nick")) {
			nick = p.getEntityData().getString("drogtor:nick");
		}
		if (p.getEntityData().hasKey("drogtor:color")) {
			nick = "§"+p.getEntityData().getString("drogtor:color")+nick;
		}
		return nick;
	}

	public static void notifyChange(String old, EntityPlayer p) {
		String nw = getColoredNick(p);
		String ann = old+" §eis now known as§r "+nw;
		for (EntityPlayer ep : (List<EntityPlayer>)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
			if (ep != p) {
				ep.sendChatToPlayer(ann);
			}
		}
		FMLCommonHandler.instance().getMinecraftServerInstance().sendChatToPlayer(ann);
		p.sendChatToPlayer("§eYour display name is now§r "+nw);
	}
	
}
