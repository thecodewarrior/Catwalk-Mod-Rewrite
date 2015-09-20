package com.thecodewarrior.catwalks.util;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.block.BlockScaffold;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;
import com.thecodewarrior.mcjty.varia.WrenchChecker;

public class CatwalkUtil {
	
	static Random rand = new Random();
	
	static boolean isDev = false;
	
	public static boolean isDev() {
		return isDev;
	}
	
	public static void init() {
		// Credit http://jabelarminecraft.blogspot.com/p/quick-tips-eclipse.html
		isDev = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
		
		WrenchChecker.init();
	}
	
	public static BlockCoord getRetractCoord(int x, int y, int z, ForgeDirection direction, Predicate<BlockCoord> p) {
		return null;
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

	public static void spawnHitParticles(float pointX, float pointY, float pointZ, ForgeDirection side, String name, World world) {
		double d  = 0.3; // random position will be between -d and +d
        
		for(int i = 0; i < 5; i++) {
			double particleX = pointX+(side.offsetX == 0 ? (rand.nextGaussian()*0.3)*d : side.offsetX*0.05);				
			double particleY = pointY+(side.offsetY == 0 ? (rand.nextGaussian()*0.3)*d : side.offsetY*0.05);
			double particleZ = pointZ+(side.offsetZ == 0 ? (rand.nextGaussian()*0.3)*d : side.offsetZ*0.05);
			CatwalkMod.proxy.spawnCustomParticle(name, world, particleX, particleY, particleZ);
//			world.spawnParticle("smoke", particleX, particleY, particleZ, 0,0,0);
		}
	}
	
	public static void extendParticles(boolean placeSucceeded, boolean didHitAnother,
			World world, int origX, int origY, int origZ,
			float hitX, float hitY, float hitZ, ForgeDirection side) {
		if(!placeSucceeded) {
        	spawnHitParticles(origX+hitX, origY+hitY, origZ+hitZ, side, "cantExtend", world);
		} else if(didHitAnother) {
			spawnHitParticles(origX+hitX, origY+hitY, origZ+hitZ, side, "hitAnother", world);
        }
	}
	
	/**
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param direction Direction to extend
	 * @param pred pred.test() returns true when it should continue extending
	 * @return the block space that finished the extension, or y=-1 if it couldn't find a stopping point
	 */
	public static BlockHit getExtendCoord(World world, int x, int y, int z, ForgeDirection direction, Predicate<BlockCoord> pred) {    	
    	int newX = x;
    	int newY = y;
    	int newZ = z;
    	for(int i = 0; i < 128; i += 1) {
			newX += direction.offsetX;
			newY += direction.offsetY;
			newZ += direction.offsetZ;
			
			Block b = world.getBlock(newX, newY, newZ);
			if(!pred.test(new BlockCoord(newX, newY, newZ))) {
				return new BlockHit(newX-direction.offsetX, newY-direction.offsetY, newZ-direction.offsetZ, direction);
			}
			if(i == 126) {
				return new BlockHit(x, y, z, ForgeDirection.UNKNOWN);
			}
		}
		return new BlockHit(x, y, z, direction.getOpposite());
	}
	
	public static boolean extendBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ,
			ItemBlock blockToPlace, Predicate<Block> shouldExtendFromBlock, Predicate<BlockCoord> continueSearching, Predicate<BlockCoord> ignoreReplaceable) {
		Block block = world.getBlock(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        
        
        int meta = world.getBlockMetadata(x, y, z);
        
        boolean isExtending = false;
        boolean placeSucceeded = false;
        boolean didHitAnother = false;

        int oldX = x;
        int oldY = y;
        int oldZ = z;
        
        if (shouldExtendFromBlock.test(block) && player.isSneaking() )
        {
    		isExtending = true;
    		
        	ForgeDirection dir = ForgeDirection.getOrientation(_side).getOpposite();
        	BlockHit coord = CatwalkUtil.getExtendCoord(world, x, y, z, dir, continueSearching);
        	x = coord.x;
        	y = coord.y;
        	z = coord.z;
        	_side = coord.side;
        	if(_side == dir.ordinal())
        		isExtending = true;
        	didHitAnother = continueSearching.test( new BlockCoord(x + (2*dir.offsetX), y + (2*dir.offsetY), z + (2*dir.offsetZ)) );
        }
        
        if(_side == ForgeDirection.UNKNOWN.ordinal()) {
        	// extension errored out, don't try to place.
        }
        else if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            _side = 1;
        }
        else if (	block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush &&
        		  (!block.isReplaceable(world, x, y, z) || ignoreReplaceable.test(new BlockCoord(x,y,z)) )
        		)
        {
            if (_side == 0)
            {
                --y;
            }

            if (_side == 1)
            {
                ++y;
            }

            if (_side == 2)
            {
                --z;
            }

            if (_side == 3)
            {
                ++z;
            }

            if (_side == 4)
            {
                --x;
            }

            if (_side == 5)
            {
                ++x;
            }
        }

        if (stack.stackSize == 0)
        {
            placeSucceeded = false;
        }
        else if (!player.canPlayerEdit(x, y, z, _side, stack))
        {
            placeSucceeded = false;
        }
        else if (y == 255 && blockToPlace.field_150939_a.getMaterial().isSolid())
        {
            placeSucceeded = false;
        }
        else if (world.canPlaceEntityOnSide(blockToPlace.field_150939_a, x, y, z, false, _side, player, stack))
        {
            int stackMeta = blockToPlace.getMetadata(stack.getItemDamage());
            int newMeta = blockToPlace.field_150939_a.onBlockPlaced(world, x, y, z, _side, hitX, hitY, hitZ, stackMeta);

            if (blockToPlace.placeBlockAt(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, newMeta))
            {
                world.playSoundEffect((double)((float)oldX + 0.5F), (double)((float)oldY + 0.5F), (double)((float)oldZ + 0.5F), blockToPlace.field_150939_a.stepSound.func_150496_b(), (blockToPlace.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, blockToPlace.field_150939_a.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }

            placeSucceeded = true;
        }
        else
        {
            placeSucceeded = false;
        }
        
        if(isExtending)
        	CatwalkUtil.extendParticles(placeSucceeded, didHitAnother, world, oldX, oldY, oldZ, hitX, hitY, hitZ, side);
        
        return placeSucceeded;
	}
	
	public static boolean retractBlock(World world, int x, int y, int z, ForgeDirection dir, EntityPlayer player,
			Predicate<BlockCoord> continueSearching) {
		int newX = x+dir.offsetX;
		int newY = y+dir.offsetY;
		int newZ = z+dir.offsetZ;
		for(int i = 0; i < 128; i++) {
			newX += dir.offsetX;
			newY += dir.offsetY;
			newZ += dir.offsetZ;
			
			Block b = world.getBlock(newX, newY, newZ);
			if(!continueSearching.test(new BlockCoord(newX, newY, newZ))){
				newX -= dir.offsetX;
				newY -= dir.offsetY;
				newZ -= dir.offsetZ;
				b = world.getBlock(newX, newY, newZ);
				List<ItemStack> drops = b.getDrops(world, newX, newY, newZ, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(newX, newY, newZ);
				for(ItemStack s : drops) {
					CatwalkUtil.giveItemToPlayer(player, s);
				}
				return true;
			}
		}
		return false;
	}
}
