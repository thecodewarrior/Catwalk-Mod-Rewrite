package com.thecodewarrior.catwalks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

public class CustomLadderRegistry {

	public static Map<Block, ICustomLadder> map = new HashMap<Block, ICustomLadder>();
	
	public static boolean isBlockCustomLadder(Block b) {
		return b instanceof ICustomLadder || map.containsKey(b);
	}
	
	public static ICustomLadder getCustomLadder(Block b) {
		if(b instanceof ICustomLadder)
			return (ICustomLadder) b;
		else
			return map.get(b);
	}
	
	public static ICustomLadder getCustomLadderOrNull(Block b) {
		ICustomLadder icl = null;
		if(isBlockCustomLadder(b))
			icl = getCustomLadder(b);
		return icl;
	}
	
	public static void registerCustomLadder(Block b, ICustomLadder ladder) {
		map.put(b, ladder);
	}
}
