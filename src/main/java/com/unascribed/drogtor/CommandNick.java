package com.unascribed.drogtor;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;

public class CommandNick extends CommandBase {

	@Override
	public String getCommandName() {
		return "nick";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		EntityPlayer p = getCommandSenderAsPlayer(ics);
		String old = Drogtor.getColoredNick(p);
		if (args.length == 0) {
			p.getEntityData().removeTag("drogtor:nick");
			Drogtor.notifyChange(old, p);
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(arg);
			sb.append(" ");
		}
		sb.setLength(sb.length()-1);
		String nick = sb.toString();
		p.getEntityData().setString("drogtor:nick", nick);
		Drogtor.notifyChange(old, p);
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof ICommand)) return -1;
		return compareNameTo((ICommand)o);
	}

}
