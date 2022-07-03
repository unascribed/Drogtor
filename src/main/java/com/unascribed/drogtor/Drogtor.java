package com.unascribed.drogtor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
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
	
	public static NBTTagCompound getPersisted(EntityPlayer p) {
		NBTTagCompound base = getPersisted(p);
		if (base.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			return base.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		}
		NBTTagCompound persisted = new NBTTagCompound();
		base.setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
		return persisted;
	}
	
	@ForgeSubscribe
	public void onChat(ServerChatEvent e) {
		if (getPersisted(e.player).hasKey("drogtor:nick") || getPersisted(e.player).hasKey("drogtor:color")) {
			String nick = getColoredNick(e.player);
			String needle = "<"+e.username+">";
			Pattern p = Pattern.compile(needle, Pattern.LITERAL);
			e.line = p.matcher(e.line).replaceFirst(Matcher.quoteReplacement("<"+nick+"§r>"));
		}
	}
	

	public static String getColoredNick(EntityPlayer p) {
		String nick = p.getCommandSenderName();
		if (getPersisted(p).hasKey("drogtor:nick")) {
			nick = getPersisted(p).getString("drogtor:nick");
		}
		if (getPersisted(p).hasKey("drogtor:color")) {
			nick = "§"+getPersisted(p).getString("drogtor:color")+nick;
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
