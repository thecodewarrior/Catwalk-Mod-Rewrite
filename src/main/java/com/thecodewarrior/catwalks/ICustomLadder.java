package com.thecodewarrior.catwalks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IBlockAccess;

public interface ICustomLadder {
	public double getLadderVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
	public double getLadderFallVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);

	public boolean isOnLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
	public boolean shouldPlayStepSound(IBlockAccess world, int x, int y, int z, EntityLivingBase entity, boolean isMovingDown);
	
	public boolean shouldStopFall(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
	
	public boolean shouldClimbDown(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
	public double getClimbDownVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity);
}
