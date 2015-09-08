package com.thecodewarrior.catwalks.block;

import java.util.ArrayList;
import java.util.List;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.ICustomLadder;
import com.thecodewarrior.catwalks.IInOutRenderSettings;
import com.thecodewarrior.catwalks.util.CatwalkUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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

public class BlockScaffold extends Block implements ICustomLadder, IInOutRenderSettings {

	IIcon side;
	IIcon top;

	IIcon inventory_top;
	IIcon inventory_side;
	
	IIcon builders_side;
	IIcon builders_top;

	IIcon builders_inventory_top;
	IIcon builders_inventory_side;
	
	public BlockScaffold() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabTransport);
		setBlockName("scaffold");
	}
	
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
		subItems.add(new ItemStack(this, 1, 0));
		subItems.add(new ItemStack(this, 1, 1));
	}
	
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
    	return world.getBlockMetadata(x,y,z) != 0;
    }
    
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {		
		ForgeDirection side = ForgeDirection.getOrientation(blockSide);
		
		if(player.getCurrentEquippedItem() != null) {
			Item item = player.getCurrentEquippedItem().getItem();
			
			if(CatwalkUtil.isHoldingWrench(player) && player.isSneaking()) {
				int newX = x+( 128*-side.offsetX );
				int newY = y+( 128*-side.offsetY );
				int newZ = z+( 128*-side.offsetZ );
				
				for(int i = 1; i < 128; i ++) {
					newX += side.offsetX;
					newY += side.offsetY;
					newZ += side.offsetZ;
					
					Block b = world.getBlock(newX, newY, newZ);
					if(b instanceof BlockScaffold){
						CatwalkUtil.giveItemsToPlayer(player, this.getDrops(world, newX, newY, newZ, world.getBlockMetadata(newX, newY, newZ), 0));
						world.setBlockToAir(newX, newY, newZ);
						break;
					}
				}
			}
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.side = reg.registerIcon(CatwalkMod.MODID + ":scaffold_side");
        this.top = reg.registerIcon(CatwalkMod.MODID + ":scaffold_top");
        
        this.inventory_top  = reg.registerIcon(CatwalkMod.MODID + ":inventory/scaffold_top");
        this.inventory_side = reg.registerIcon(CatwalkMod.MODID + ":inventory/scaffold_side");
        
        this.builders_side = reg.registerIcon(CatwalkMod.MODID + ":scaffold_builders_side");
        this.builders_top = reg.registerIcon(CatwalkMod.MODID + ":scaffold_builders_top");
        
        this.builders_inventory_top  = reg.registerIcon(CatwalkMod.MODID + ":inventory/scaffold_builders_top");
        this.builders_inventory_side = reg.registerIcon(CatwalkMod.MODID + ":inventory/scaffold_builders_side");
    }
	
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int _side, int meta) {
    	
    	if(_side >= 100) {
    		ForgeDirection side = ForgeDirection.getOrientation(_side - 100);
    		if(side == ForgeDirection.UP || side == ForgeDirection.DOWN)
            	return meta == 0 ? this.inventory_top : this.builders_inventory_top;
            return meta == 0 ? this.inventory_side : this.builders_inventory_side;
    	}
    	
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        if(side == ForgeDirection.UP || side == ForgeDirection.DOWN)
        	return meta == 0 ? this.top : this.builders_top;
        return meta == 0 ? this.side : this.builders_side;
    }
    
    @SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int _side) {
    	ForgeDirection side = ForgeDirection.getOrientation(_side);
    	
    	return !w.isSideSolid(x, y, z, side, false);
	}
    
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    	ArrayList<ItemStack> arr = new ArrayList<ItemStack>();
    	arr.add( new ItemStack(Item.getItemFromBlock(this), 1, metadata) );
    	return arr;
    }
    
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return false;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
    	CatwalkUtil.giveItemsToPlayer(player, this.getDrops(world, x, y, z, meta, 0));
    }
    
    //==============================================================================
  	// Render type methods
  	//==============================================================================
  	
  	public int getRenderType(){
  	    return CatwalkMod.inAndOutRenderType;
  	}
  	
  	@SideOnly(Side.CLIENT)
  	public boolean isBlockNormalCube()
  	{
  	    return false;
  	}
  	
  	public boolean isNormalCube()
  	{
  	    return false;
  	}
  	
  	public boolean isOpaqueCube()
  	{
  	    return false;
  	}
    

	@Override
	public double getLadderVelocity(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return entity.isSneaking() ? 0 : 0.15;
	}

	@Override
	public double getLadderFallVelocity(IBlockAccess world, int x, int y,
			int z, EntityLivingBase entity) {
		return 0.3;
	}

	@Override
	public boolean isOnLadder(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean shouldPlayStepSound(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity, boolean isMovingDown) {
		return true;
	}

	@Override
	public boolean shouldHoldOn(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		boolean isHoldingScaffold = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			if(player.getHeldItem() != null) {
				Item item = player.getHeldItem().getItem();
				if(item instanceof ItemBlock && ((ItemBlock)item).field_150939_a instanceof BlockScaffold)
					isHoldingScaffold = true;
			}
		}
		return entity.isSneaking() || isHoldingScaffold;
	}

	@Override
	public boolean shouldClimbDown(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		boolean isHoldingScaffold = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			if(player.getHeldItem() != null) {
				Item item = player.getHeldItem().getItem();
				if(item instanceof ItemBlock && ((ItemBlock)item).field_150939_a instanceof BlockScaffold)
					isHoldingScaffold = true;
			}
		}
		return entity.isSneaking() && isHoldingScaffold;
	}

	@Override
	public double getClimbDownVelocity(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return 0.15;
	}

	@Override
	public boolean shouldForceBackFaceRender() {
		return false;
	}

}
