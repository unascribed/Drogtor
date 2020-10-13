package com.unascribed.drogtor;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Formatting;

public interface DrogtorPlayer {

	void drogtor$setNickname(@Nullable String nickname);
	void drogtor$setNameColor(@Nullable Formatting fmt);
	
	@Nullable String drogtor$getNickname();
	@Nullable Formatting drogtor$getNameColor();
	
	boolean drogtor$isActive();
	
}
