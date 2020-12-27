package com.unascribed.drogtor;

public abstract class ConvertedModInitializer {

	public ConvertedModInitializer() {
		onInitialize();
	}
	
	public abstract void onInitialize();
	
}
