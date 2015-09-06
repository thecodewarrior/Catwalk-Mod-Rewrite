package com.thecodewarrior.catwalks.block;

import java.util.List;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.ISturdyTrackExtendable;
import com.thecodewarrior.catwalks.util.CatwalkUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
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
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSturdyRailBooster extends BlockRailPowered implements ISturdyTrackExtendable{

	public IIcon on;
	public IIcon off;
		
	public BlockSturdyRailBooster() {
	  this.setCreativeTab(CreativeTabs.tabTransport);
		this.setBlockName("sturdy_booster_rail");
		this.setHardness(0.7F);
	}

	public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
	  return !field_150053_a;
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if(CatwalkUtil.isHoldingWrench(player)) {
			if(player.isSneaking()) {

				List<ItemStack> drops = this.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
				for(ItemStack s : drops) {
					CatwalkUtil.giveItemToPlayer(player, s);
				}
			}
		}
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
		
		if(held != null && held.getItem() instanceof IToolWrench) {
			int meta = world.getBlockMetadata(x,y,z);
			if((meta & 8) == 0) {
				meta = meta | 8;
			} else {
				meta = meta & ~8;
			}
			world.setBlockMetadataWithNotify(x, y, z, meta, 3);
		}
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
	
	public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        int railMeta = this.getBasicRailMetadata(world, cart, x, y, z);
        int meta = world.getBlockMetadata(x,y,z);
		double d15 = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);

		if( (meta & 8) == 0)
			return;
		
        if (d15 > 0.01D)
        {
            double d16 = 0.06D;
            cart.motionX += cart.motionX / d15 * d16;
            cart.motionZ += cart.motionZ / d15 * d16;
        }
        else if (railMeta == 1)
        {
            if (cart.worldObj.getBlock(x - 1, y, z).isNormalCube())
            {
                cart.motionX = 0.02D;
            }
            else if (cart.worldObj.getBlock(x + 1, y, z).isNormalCube())
            {
                cart.motionX = -0.02D;
            }
        }
        else if (railMeta == 0)
        {
            if (cart.worldObj.getBlock(x, y, z - 1).isNormalCube())
            {
                cart.motionZ = 0.02D;
            }
            else if (cart.worldObj.getBlock(x, y, z + 1).isNormalCube())
            {
                cart.motionZ = -0.02D;
            }
        }
    }
	
	public boolean isPowered() {
		return true;
	}
	
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z); // base Block class behavior
    }
	  
	public float getRailMaxSpeed(World world, EntityMinecart cart, int y, int x, int z)
	{
	    return super.getRailMaxSpeed(world, cart, y, x, z);
	}
	
	@SideOnly(Side.CLIENT)
    public String getItemIconName()
    {
        return CatwalkMod.MODID + ":blocks/sturdy_rail_booster";
    }
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
    	return (p_149691_2_ & 8) == 0 ? off : on;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.on   = reg.registerIcon("catwalks:sturdy_rail_booster_on");
        this.off = reg.registerIcon("catwalks:sturdy_rail_booster_off");
    }
	
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block b)
    {
        if (!world.isRemote)
        {
            int l = world.getBlockMetadata(x, y, z);
            int i1 = l;

            if (this.field_150053_a)
            {
                i1 = l & 7;
            }
            func_150052_a(world, x, y, z, (l & 8) > 1);
            //            this.func_150048_a(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, l, i1, p_149695_5_);
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
