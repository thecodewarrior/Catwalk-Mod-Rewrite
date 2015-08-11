package com.thecodewarrior.catwalks;

import net.minecraft.launchwrapper.Launch;

public class CatwalkOptions {
	
	public boolean fullBlockLadder = false;
	public boolean dev = false;
	
	public void init() {
		// Credit http://jabelarminecraft.blogspot.com/p/quick-tips-eclipse.html
		dev = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}
	
	public void load() {
		
	}

}
