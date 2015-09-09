package com.thecodewarrior.catwalks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.block.BlockCagedLadder;
import com.thecodewarrior.catwalks.block.BlockCatwalk;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSupportColumn extends ItemBlock {

	public ItemBlockSupportColumn(Block b) {
		super(b);
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        if(( block instanceof BlockCatwalk || block instanceof BlockCagedLadder ) && world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ) instanceof BlockSupportColumn) {
        	x += side.offsetX;
        	y += side.offsetY;
        	z += side.offsetZ;
        	hitX -= side.offsetX;
        	hitY -= side.offsetY;
        	hitZ -= side.offsetZ;
        	
        	_side = side.getOpposite().ordinal();
        	block = world.getBlock(x,y,z);
        }
        
        int meta = world.getBlockMetadata(x, y, z);
        
        boolean isExtending = false;
        boolean isNextBlockSupport = false;
        boolean ret = false;
        
        int oldX = x;
        int oldY = y;
        int oldZ = z;
        
        if (block instanceof BlockSupportColumn && player.isSneaking() )//&& isOnCorrectSide)
        {
    		isExtending = true;
        	ForgeDirection dir = ForgeDirection.getOrientation(_side).getOpposite();
        	
        	int newX = x;
        	int newY = y;
        	int newZ = z;
        	boolean use = false;
        	for(int i = 0; i < 128; i += 1) {
				newX += dir.offsetX;
				newY += dir.offsetY;
				newZ += dir.offsetZ;
				
				Block b = world.getBlock(newX, newY, newZ);
				if(!( b instanceof BlockSupportColumn )) {
					x = newX-dir.offsetX;
					y = newY-dir.offsetY;
					z = newZ-dir.offsetZ;
					_side = dir.ordinal();
					
					newX += dir.offsetX;
					newY += dir.offsetY;
					newZ += dir.offsetZ;
					if(world.getBlock(newX, newY, newZ) instanceof BlockSupportColumn) {
						isNextBlockSupport = true;
					}
					break;
				}
			}
        }
        
        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            _side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
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
            ret = false;
        }
        else if (!player.canPlayerEdit(x, y, z, _side, stack))
        {
            ret = false;
        }
        else if (y == 255 && this.field_150939_a.getMaterial().isSolid())
        {
            ret = false;
        }
        else if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, _side, player, stack))
        {
            int stackMeta = this.getMetadata(stack.getItemDamage());
            int newMeta = this.field_150939_a.onBlockPlaced(world, x, y, z, _side, hitX, hitY, hitZ, stackMeta);

            if (placeBlockAt(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, newMeta))
            {
                world.playSoundEffect((double)((float)oldX + 0.5F), (double)((float)oldY + 0.5F), (double)((float)oldZ + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getPitch() * 0.8F);
                --stack.stackSize;
            }

            ret = true;
        }
        else
        {
            ret = false;
        }
        
        if(!ret && isExtending) {
	        double d  = 0.3; // random position will be between -d and +d
	        
			for(int i = 0; i < 10; i++) {
				double particleX = oldX+hitX+(side.offsetX == 0 ? (Math.random()-0.5)*d : 0);				
				double particleY = oldY+hitY+(side.offsetY == 0 ? (Math.random()-0.5)*d : 0);
				double particleZ = oldZ+hitZ+(side.offsetZ == 0 ? (Math.random()-0.5)*d : 0);
				world.spawnParticle("smoke", particleX, particleY, particleZ, 0,0,0);
			}
		} else if(isNextBlockSupport) {
	        double d  = 0.3; // random position will be between -d and +d
	        
	    	for(int i = 0; i < 10; i++) {
				double particleX = oldX+hitX+(side.offsetX == 0 ? (Math.random()-0.5)*d : 0);				
				double particleY = oldY+hitY+(side.offsetY == 0 ? (Math.random()-0.5)*d : 0);
				double particleZ = oldZ+hitZ+(side.offsetZ == 0 ? (Math.random()-0.5)*d : 0);
				world.spawnParticle("crit", particleX, particleY, particleZ, 0,0,0);
			}
        }
        
        return ret;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int _side, EntityPlayer player, ItemStack stack)
    {
    	/*
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        
        int oldX = x;
        int oldY = y;
        int oldZ = z;
        
        if (block instanceof BlockSupportColumn && player.isSneaking() )//&& isOnCorrectSide)
        {
        	ForgeDirection dir = ForgeDirection.getOrientation(_side).getOpposite();
        	
        	int newX = x;
        	int newY = y;
        	int newZ = z;
        	boolean use = false;
        	for(int i = 0; i < 128; i += 1) {
				newX += dir.offsetX;
				newY += dir.offsetY;
				newZ += dir.offsetZ;
				
				Block b = world.getBlock(newX, newY, newZ);
				if(!( b instanceof BlockSupportColumn )) {
					x = newX-dir.offsetX;
					y = newY-dir.offsetY;
					z = newZ-dir.offsetZ;
					_side = dir.ordinal();
					break;
				}
			}
        }
        
        if (block == Blocks.snow_layer)
        {
            _side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && (
        		!block.isReplaceable(world, x, y, z) ||
        		block.getClass().isAssignableFrom(field_150939_a.getClass())))
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

        return world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, _side, (Entity)null, stack);
        */
    	return true;
    }
	
}
