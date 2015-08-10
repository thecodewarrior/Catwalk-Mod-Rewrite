package com.thecodewarrior.catwalks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IBlockAccess;

public interface ICustomLadderVelocity {
	public double getLadderVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
	public double getLadderFallVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);

	public boolean isOnLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
}
