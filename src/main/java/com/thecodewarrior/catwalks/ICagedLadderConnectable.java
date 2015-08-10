package com.thecodewarrior.catwalks;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICagedLadderConnectable {
	public boolean shouldConnectToSide(IBlockAccess w, int x, int y, int z, ForgeDirection side);
	public boolean shouldHaveBottom(IBlockAccess w, int x, int y, int z, ForgeDirection side);
	public boolean doesSideHaveWall(IBlockAccess w, int x, int y, int z, ForgeDirection side);
	public boolean isThin(IBlockAccess w, int x, int y, int z, ForgeDirection side);
}
