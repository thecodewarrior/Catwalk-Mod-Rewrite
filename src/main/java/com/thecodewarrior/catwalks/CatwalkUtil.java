package com.thecodewarrior.catwalks;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.Vec3;
import buildcraft.api.tools.IToolWrench;

public class CatwalkUtil {
	
	static boolean isDev = false;
	
	public static boolean isDev() {
		return isDev;
	}
	
	public static void init() {
		// Credit http://jabelarminecraft.blogspot.com/p/quick-tips-eclipse.html
		isDev = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
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
			if(item instanceof IToolWrench)
				return true;
			Set<String> toolClasses = player.getHeldItem().getItem().getToolClasses(player.getHeldItem());
			if(toolClasses.contains("wrench"))
				return true;
		}
		return false;
	}
	
	public static void giveItemToPlayer(EntityPlayer player, ItemStack _stack) {
		if(player.capabilities.isCreativeMode)
			return;
		ItemStack stack = _stack.copy();
		if(player.inventory.addItemStackToInventory(stack)) {
			player.inventoryContainer.detectAndSendChanges();
		} else if( !player.worldObj.isRemote ){
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
