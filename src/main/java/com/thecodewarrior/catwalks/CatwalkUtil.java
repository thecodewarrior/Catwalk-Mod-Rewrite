package com.thecodewarrior.catwalks;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
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
	
	public static CatwalkEntityProperties getOrCreateEP(Entity e) {
		CatwalkEntityProperties catwalkEP = (CatwalkEntityProperties)e.getExtendedProperties("catwalkmod.catwalkdata");
		if(catwalkEP == null) {
			catwalkEP = new CatwalkEntityProperties();
			e.registerExtendedProperties("catwalkmod.catwalkdata", catwalkEP);
		}
		return catwalkEP;
	}
	
	public static ForgeDirection getHorizontalFacingDirection(EntityPlayer player) {
		int dir = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		switch(dir) {
		case 0:
			return ForgeDirection.SOUTH;
		case 1:
			return ForgeDirection.WEST;
		case 2:
			return ForgeDirection.NORTH;
		case 3:
			return ForgeDirection.EAST;
		}
		
		return null;
	}
	
	public static ForgeDirection getVerticalFacingDirection(EntityPlayer player) {
		float rot = player.rotationPitch;
		if(rot > 60)
			return ForgeDirection.DOWN;
		if(rot < -60)
			return ForgeDirection.UP;
		return null;
	}
	
	public static ForgeDirection getFacingDirection(EntityPlayer player) {
		ForgeDirection dir = getVerticalFacingDirection(player);
		if(dir == null)
			dir = getHorizontalFacingDirection(player);
		return dir;
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

	public static void consumeFromStack(EntityPlayer player, ItemStack stack) {
		if(player.capabilities.isCreativeMode)
			return;
		else {
			stack.stackSize--;
			if(stack.stackSize <= 0) {
				for(int i = 0; i < player.inventory.mainInventory.length; i++) {
					if(player.inventory.mainInventory[i] == stack) {
						player.inventory.mainInventory[i] = null;
					}
				}
			}
		}
		
	}
}
