package com.thecodewarrior.catwalks;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class CatwalkEntityProperties implements IExtendedEntityProperties {

	public int timeout;
	public double multiplier;
	public boolean isInList = false;
	public boolean highSpeedLadder = false;
	public double lastStepX;
	public double lastStepY;
	public double lastStepZ;
	public boolean isSlidingDownLadder = false;
	public double lastPosX;
	public double lastPosY;
	public double lastPosZ;
	public boolean currentUseKeyState;
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {}

	@Override
	public void loadNBTData(NBTTagCompound compound) {}

	@Override
	public void init(Entity entity, World world) {
		timeout = -10;
		multiplier = 0;
		lastStepX = entity.posX;
		lastStepY = entity.posY;
		lastStepZ = entity.posZ;
		lastPosX = entity.posX;
		lastPosY = entity.posY;
		lastPosZ = entity.posZ;
	}

}
