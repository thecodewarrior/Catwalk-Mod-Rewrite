package com.thecodewarrior.catwalks.util;

import net.minecraftforge.common.util.ForgeDirection;

public class BlockHit {
	int x, y, z, side;
	
	public BlockHit(int x, int y, int z, int side) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}
	
	public BlockHit(int x, int y, int z, ForgeDirection side) {
		this(x, y, z, side.ordinal());
	}

}
