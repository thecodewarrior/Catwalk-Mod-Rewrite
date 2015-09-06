package com.thecodewarrior.catwalks.util;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.Vec3;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.mcjty.varia.WrenchChecker;

public class CatwalkUtil {
	
	static boolean isDev = false;
	
	public static boolean isDev() {
		return isDev;
	}
	
	public static void init() {
		// Credit http://jabelarminecraft.blogspot.com/p/quick-tips-eclipse.html
		isDev = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
		
		WrenchChecker.init();
	}
	
	public static EntityPlayer getPlayerLooking(Vec3 start, Vec3 end) {
		return CatwalkMod.proxy.getPlayerLooking(start, end); // different on client/server
	}
	
	public static boolean isHoldingWrench(EntityLivingBase entity, boolean defult) {
		if(entity instanceof EntityPlayer) {
			return isHoldingWrench( (EntityPlayer) entity );
		}
		return defult;
	}
	
	public static boolean isHoldingWrench(EntityPlayer player) {
		ItemStack heldStack = player.getHeldItem();
		if(heldStack != null) {
			Item item = heldStack.getItem();
			if(WrenchChecker.isAWrench(item))
				return true;
			if(item.getToolClasses(heldStack).contains("wrench"))
				return true;
		}
		return false;
	}
	
	public static void giveItemsToPlayer(EntityPlayer player, List<ItemStack> stacks) {
		for(ItemStack s : stacks) {
			giveItemToPlayer(player, s);
		}
	}
	
	public static void giveItemToPlayer(EntityPlayer player, ItemStack _stack) {
		if(player.capabilities.isCreativeMode)
			return;
		ItemStack stack = _stack.copy();
		if(player.inventory.addItemStackToInventory(stack)) {
			player.inventoryContainer.detectAndSendChanges();
		}
		if( !player.worldObj.isRemote && stack.stackSize > 0){
			double xSize = player.boundingBox.maxX - player.boundingBox.minX;
			double ySize = player.boundingBox.maxY - player.boundingBox.minY;
			double zSize = player.boundingBox.maxZ - player.boundingBox.minZ;
			//spawn item in center of player's body
			EntityItem entity = new EntityItem(player.worldObj, player.posX, player.posY+0.5, player.posZ, stack);
			entity.delayBeforeCanPickup = 0;
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;
			player.worldObj.spawnEntityInWorld(entity);
		}
	}
}
