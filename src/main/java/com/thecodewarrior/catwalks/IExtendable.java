package com.thecodewarrior.catwalks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IExtendable {

	public boolean extend(World world, int x, int y, int z, EntityPlayer player);
	
}
