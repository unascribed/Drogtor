package com.unascribed.drogtor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.src.CommandBase;
import net.minecraft.src.CommandException;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;

public class CommandColor extends CommandBase {

	@Override
	public String getCommandName() {
		return "color";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer p = getCommandSenderAsPlayer(ics);
		if (args.length == 0) {
			Drogtor.getPersisted(p).removeTag("drogtor:color");
			p.sendChatToPlayer("§eYour display name is now§r "+Drogtor.getColoredNick(p));
			return;
		}
		String color;
		switch (args[0]) {
			case "black": case "0":
				throw new CommandException("Black is not a permitted name color");
			case "dark_blue": case "1":
				color = "1";
				break;
			case "dark_green": case "2":
				color = "2";
				break;
			case "dark_aqua": case "3":
				color = "3";
				break;
			case "dark_red": case "4":
				color = "4";
				break;
			case "dark_purple": case "5":
				color = "5";
				break;
			case "gold": case "6":
				color = "6";
				break;
			case "gray": case "7":
				color = "7";
				break;
			case "dark_gray": case "8":
				color = "8";
				break;
			case "blue": case "9":
				color = "9";
				break;
			case "green": case "a":
				color = "a";
				break;
			case "aqua": case "b":
				color = "b";
				break;
			case "red": case "c":
				color = "c";
				break;
			case "light_purple": case "d":
				color = "d";
				break;
			case "yellow": case "e":
				color = "e";
				break;
			case "white": case "f":
				color = "f";
				break;
			default:
				throw new CommandException("Unknown color");
		}
		Drogtor.getPersisted(p).setString("drogtor:color", color);
		p.sendChatToPlayer("§eYour display name is now§r "+Drogtor.getColoredNick(p));
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings) {
		if (strings.length > 0) return Collections.EMPTY_LIST;
		List<String> li = new ArrayList<>();
		li.add("black");
		li.add("dark_blue");
		li.add("dark_green");
		li.add("dark_aqua");
		li.add("dark_red");
		li.add("dark_purple");
		li.add("gold");
		li.add("gray");
		li.add("dark_gray");
		li.add("blue");
		li.add("green");
		li.add("aqua");
		li.add("red");
		li.add("light_purple");
		li.add("yellow");
		li.add("white");
		return li;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof ICommand)) return -1;
		return compareNameTo((ICommand)o);
	}

}
