package com.thecodewarrior.catwalks.item;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.block.BlockScaffold;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.catwalks.util.Predicate;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockScaffold extends ItemBlock {

	Random rand = new Random();
	
	public ItemBlockScaffold(Block p_i45328_1_) {
		super(p_i45328_1_);
		this.setHasSubtypes(true);
	}
	
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.field_150939_a.getUnlocalizedName() + ( stack.getItemDamage() != 0 ? ".builder" : "" );
    }
    
    public int getMetadata(int damage)
    {
        return damage == 0 ? 0 : 1; // limit meta to 0 and 1
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ)
    {
    	
    	return CatwalkUtil.extendBlock(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, this,
    	        new Predicate<Block>() { public boolean test(Block block) { /*---*/ return block instanceof BlockScaffold; /*---*/ } },
    	        new Predicate<BlockCoord>(world) {
    				public boolean test(BlockCoord coord) {
    					World world = (World)args[0];
    					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockScaffold;
    				}
    	    	},
    	    	new Predicate<BlockCoord>(world) {
    	    		public boolean test(BlockCoord coord) {
    	    			World world = (World)args[0];
    					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockScaffold;
    	    		}
    	    	});
    	
    	/*
        Block block = world.getBlock(x, y, z);

        boolean isExtending = false;
        boolean isNextBlockScaffold = false;
        boolean ret = false;
        
        int oldX = x;
        int oldY = y;
        int oldZ = z;
        
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        if (block instanceof BlockScaffold && player.isSneaking())
        {
    		isExtending = true;
        	ForgeDirection dir = side.getOpposite();
        	int newX = x;
        	int newY = y;
        	int newZ = z;
        	boolean use = false;
        	for(int i = 0; i < 128; i += 1) {
				newX += dir.offsetX;
				newY += dir.offsetY;
				newZ += dir.offsetZ;
				
				Block b = world.getBlock(newX, newY, newZ);
				if(!( b instanceof BlockScaffold )) {
					x = newX-dir.offsetX;
					y = newY-dir.offsetY;
					z = newZ-dir.offsetZ;
					_side = dir.ordinal();
					block = world.getBlock(x,y,z);
					newX += dir.offsetX;
					newY += dir.offsetY;
					newZ += dir.offsetZ;
					if(world.getBlock(newX, newY, newZ) instanceof BlockScaffold) {
						isNextBlockScaffold = true;
					}
					
					break;
				}
			}
        }
        
        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            _side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && ( !block.isReplaceable(world, x, y, z) || block instanceof BlockScaffold))
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
            int meta = this.field_150939_a.onBlockPlaced(world, x, y, z, _side, hitX, hitY, hitZ, stackMeta);

            if (placeBlockAt(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, meta))
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
        	CatwalkUtil.spawnHitParticles(oldX+hitX, oldY+hitY, oldZ+hitZ, side, "cantExtend", world);
		} else if(isNextBlockScaffold) {
        	CatwalkUtil.spawnHitParticles(oldX+hitX, oldY+hitY, oldZ+hitZ, side, "crit", world);
        }
			
        return ret; */
    }
    
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack)
    {
    	/*
        Block block = world.getBlock(x, y, z);
        
        if (block instanceof BlockScaffold && player.isSneaking())
        {
        	ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
        	int newX = x;
        	int newY = y;
        	int newZ = z;
        	boolean use = false;
        	for(int i = 0; i < 128; i += 1) {
				newX += dir.offsetX;
				newY += dir.offsetY;
				newZ += dir.offsetZ;
				
				Block b = world.getBlock(newX, newY, newZ);
				if(!( b instanceof BlockScaffold )) {
					x = newX-dir.offsetX;
					y = newY-dir.offsetY;
					z = newZ-dir.offsetZ;
					side = dir.ordinal();
					block = world.getBlock(x,y,z);
					break;
				}
			}
        }
        
        if (block == Blocks.snow_layer)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && (
        		!block.isReplaceable(world, x, y, z) ||
        		block.getClass().isAssignableFrom(field_150939_a.getClass())))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        return world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, (Entity)null, stack); */
    	return true;
    }
	
}
