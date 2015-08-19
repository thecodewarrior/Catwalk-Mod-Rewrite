package com.thecodewarrior.catwalks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.codechicken.lib.raytracer.RayTracer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSturdyTrack extends BlockRailBase {

	RayTracer rayTracer = new RayTracer();
	
	public IIcon straight;
	public IIcon curved;
	
	public BlockSturdyTrack(boolean p_i45389_1_) {
		super(p_i45389_1_);
		setBlockName("sturdy_track");
	}

	 /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World w, int x, int y, int z)
    {
        return w.getBlock(x, y, z).isReplaceable(w, x, y, z); // base Block class behavior
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
    
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_2_ >= 6 ? curved : straight;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.straight = reg.registerIcon("catwalks:sturdy_rail");
        this.curved = reg.registerIcon("catwalks:sturdy_rail_turned");
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
		
		if(blockSide == ForgeDirection.UP.ordinal() && held != null && held.getItem() == ItemBlock.getItemFromBlock(this)) {
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
	
	/**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_)
    {
        this.setBlockBoundsBasedOnState(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_);
        return super.collisionRayTrace(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_, p_149731_5_, p_149731_6_);
    }
    
    /**
     * Returns the max speed of the rail at the specified position.
     * @param world The world.
     * @param cart The cart on the rail, may be null.
     * @param x The rail X coordinate.
     * @param y The rail Y coordinate.
     * @param z The rail Z coordinate.
     * @return The max speed of the current rail.
     */
    public float getRailMaxSpeed(World world, EntityMinecart cart, int y, int x, int z)
    {
        return 0.6f;
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
	
}
