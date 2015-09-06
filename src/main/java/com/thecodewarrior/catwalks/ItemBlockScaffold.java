package com.thecodewarrior.catwalks;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemBlockScaffold extends ItemBlock {

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

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(x, y, z);

        boolean isExtending = false;
        boolean ret = false;
        
        int oldX = x;
        int oldY = y;
        int oldZ = z;
        
        if (block instanceof BlockScaffold && player.isSneaking())
        {
    		isExtending = true;
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
					break;
				}
			}
        }
        
        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && ( !block.isReplaceable(world, x, y, z) || block instanceof BlockScaffold))
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

        if (stack.stackSize == 0)
        {
            ret = false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            ret = false;
        }
        else if (y == 255 && this.field_150939_a.getMaterial().isSolid())
        {
            ret = false;
        }
        else if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, player, stack))
        {
            int stackMeta = this.getMetadata(stack.getItemDamage());
            int meta = this.field_150939_a.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, stackMeta);

            if (placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, meta))
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
        	ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
	        double d = 0.2; // random values will be between -d and +d
			for(int i = 0; i < 10; i++) {
				double particleX = oldX+(Math.random()*d) + (dir.offsetX!=0 ? (  (dir.offsetX > 0 ? 1.15 : -0.15)  ) :0.5);				
				double particleY = oldY+(Math.random()*d) + (dir.offsetY!=0 ? (  (dir.offsetY > 0 ? 1.15 : -0.15)  ) :0.5);
				double particleZ = oldZ+(Math.random()*d) + (dir.offsetZ!=0 ? (  (dir.offsetZ > 0 ? 1.15 : -0.15)  ) :0.5);
				world.spawnParticle("smoke", particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
			}
		}
        return ret;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack)
    {
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

        return world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, (Entity)null, stack);
    }
	
}
