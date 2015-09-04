package com.thecodewarrior.catwalks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockScaffold extends Block implements ICustomLadder {

	IIcon side;
	IIcon top;
	
	public BlockScaffold() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabTransport);
		setBlockName("scaffold");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.side = reg.registerIcon(CatwalkMod.MODID + ":" + "scaffold_side");
        this.top = reg.registerIcon(CatwalkMod.MODID + ":" + "scaffold_top");
    }
	
	
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ForgeDirection d = ForgeDirection.getOrientation(side);
        
        if(d == ForgeDirection.UP || d == ForgeDirection.DOWN)
        	return this.top;
        return this.side;
    }
    
    @SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int _side)
	{
    	ForgeDirection side = ForgeDirection.getOrientation(_side);
    	
    	return !w.isSideSolid(x, y, z, side, false);
	}
    
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {
    	CatwalkMod.l.info("hey");
    	return false;
    }
    
    public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_) {
    	CatwalkMod.l.info("onBlockDestroyedByPlayer");
    }
    
    public void breakBlock(World world, int x, int y, int z, Block b, int p_149749_6_) {
    	super.breakBlock(world, x, y, z, b, p_149749_6_);
    	ItemStack stack = new ItemStack(CatwalkMod.scaffold.getItem(world, x, y, z), 1);
    	int d = 10;
    	List<EntityPlayer> l = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x-d, y-d, z-d, x+d, y+d, z+d));
    	EntityPlayer closest = null;
    	double dist = Integer.MAX_VALUE;
    	for(EntityPlayer p : l) {
    		double playerDist = MathHelper.sqrt_double(
    				( (p.posX - x)*(p.posX - x) ) +
    				( (p.posY - y)*(p.posY - y) ) +
    				( (p.posZ - z)*(p.posZ - z) )
    		);
    		if(playerDist < dist) {
    			closest = p;
    			dist = playerDist;
    		}
    	}
    	if(closest == null) {
    		EntityItem ent = new EntityItem(world, x+0.5, y+0.5, z+0.5, stack);
    		world.spawnEntityInWorld(ent);
    	} else {
    		CatwalkUtil.giveItemToPlayer(closest, stack);
    	}
    }


    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
    	return true;
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
		return 0.15;
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
		return entity.isSneaking();
	}

	@Override
	public boolean shouldClimbDown(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return false;
	}

	@Override
	public double getClimbDownVelocity(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return 0.15;
	}

}
