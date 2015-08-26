package com.thecodewarrior.catwalks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSturdyRailDetector extends BlockRailDetector implements ISturdyTrackExtendable {

//	private int renderType = 9;
	public IIcon straight;
	public IIcon curved;
		
	public BlockSturdyRailDetector() {
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setBlockName("sturdy_detector_rail");
		this.setHardness(0.7F);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {
		int l = MathHelper.floor_double((double)((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		ForgeDirection d = ForgeDirection.NORTH;
		switch(l) {
		case 0:
			d = ForgeDirection.SOUTH; break;
		case 1:
			d = ForgeDirection.WEST;  break;
		case 2:
			d = ForgeDirection.NORTH; break;
		case 3:
			d = ForgeDirection.EAST;  break;
		}
		
		ItemStack held = player.getCurrentEquippedItem();
		
		if(blockSide == ForgeDirection.UP.ordinal() && held != null && held.getItem() instanceof ItemBlock && ( (ItemBlock) held.getItem()).field_150939_a instanceof ISturdyTrackExtendable) {
			if(this.canPlaceBlockAt(world, x+d.offsetX, y+d.offsetY, z+d.offsetZ)) {
				ItemBlock ib = (ItemBlock) held.getItem();
				if(ib.placeBlockAt(held, player, world, x+d.offsetX, y+d.offsetY, z+d.offsetZ, d.getOpposite().ordinal(),
						d.offsetX > 0 ? 1 : 0.5F, 1/32F, d.offsetZ > 0 ? 1 : 0.5F, held.getItemDamage())
					&& !player.capabilities.isCreativeMode
					)
					held.stackSize--;
			}
			return true;
		}
		return false;
	}
//	
//	public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
//	  return false;
//	}

	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z) && !( world.getBlock(x, y-1, z) instanceof BlockRailBase);
    }
	  
	public float getRailMaxSpeed(World world, EntityMinecart cart, int y, int x, int z)
	{
	    return super.getRailMaxSpeed(world, cart, y, x, z);
	}
	
	/**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
    	return p_149691_2_ >= 6 ? curved : straight;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.straight  = reg.registerIcon("catwalks:sturdy_rail_detector");
        this.curved    = reg.registerIcon("catwalks:sturdy_rail_detector_powered");
    }
	
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!p_149695_1_.isRemote)
        {
            int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_);
            int i1 = l;

            if (this.field_150053_a)
            {
                i1 = l & 7;
            }
            this.func_150048_a(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, l, i1, p_149695_5_);
        }
    }
    
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB blockBounds, List list, Entity entity) {
    	if(entity instanceof EntityMinecart)
    		return;
    	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0, x + 1, y + (1/16F), z + 1);
    	if (aabb != null && blockBounds.intersectsWith(aabb))
        {
            list.add(aabb);
        }
    }
}
