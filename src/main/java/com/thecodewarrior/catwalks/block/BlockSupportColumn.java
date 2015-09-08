package com.thecodewarrior.catwalks.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.ICustomLadder;
import com.thecodewarrior.catwalks.IInOutRenderSettings;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.codechicken.lib.raytracer.RayTracer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSupportColumn extends Block implements ICustomLadder, IInOutRenderSettings {

	RayTracer raytracer = new RayTracer();
	
	IIcon inventory;	
	public IIcon support;
	
	public BlockSupportColumn() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabTransport);
		setBlockName("support_column");
		float d = 3/16F;
		setBlockBounds(   d, 0,   d,
						1-d, 1, 1-d);
		setHardness(1.0F);
	}

	public double getWidth() {
		return 10/16F;
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
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {		
		ForgeDirection side = ForgeDirection.getOrientation(blockSide);
		
		if(player.getCurrentEquippedItem() != null) {
			Item item = player.getCurrentEquippedItem().getItem();
			boolean use = false;
			
			if(CatwalkUtil.isHoldingWrench(player)) {
				if(player.isSneaking()) {
					ForgeDirection opp = side.getOpposite();
					for(int i = 128; i > 0; i--) {
						int newX = x + ( i*opp.offsetX );
						int newY = y + ( i*opp.offsetY );
						int newZ = z + ( i*opp.offsetZ );
						
						Block b = world.getBlock(newX, newY, newZ);
						if(b instanceof BlockSupportColumn){
							List<ItemStack> drops = b.getDrops(world, newX, newY, newZ, world.getBlockMetadata(x, y+i, z), 0);
							world.setBlockToAir(newX, newY, newZ);
							for(ItemStack s : drops) {
								CatwalkUtil.giveItemToPlayer(player, s);
							}
							break;
						}
					}
				} else {
					int meta = world.getBlockMetadata(x,y,z);
					if(meta < 3) {
						world.setBlockMetadataWithNotify(x, y, z, 3, 3);
					} else {
						int newMeta = 0;
						if(side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
							newMeta = 0;
						}
						if(side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH) {
							newMeta = 1;
						}
						if(side == ForgeDirection.EAST || side == ForgeDirection.WEST){ 
							newMeta = 2;
						}
						world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister reg) {
        
        this.inventory  = reg.registerIcon(CatwalkMod.MODID + ":inventory/support");
        
        this.support = reg.registerIcon(CatwalkMod.MODID + ":support");
	}
	
	@Override
	public IIcon getIcon(int _side, int meta) {
		
		if(_side >= 100) {
    		ForgeDirection side = ForgeDirection.getOrientation(_side - 100);
            return this.inventory;
    	}
		return this.support;
	}
	
	public boolean isOpaqueCube() {
		return false;
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
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
    	return true;
    }
    
    public boolean isSideEnd(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    	int meta = world.getBlockMetadata(x, y, z);
    	if(meta == 0 && ( side == ForgeDirection.UP    || side == ForgeDirection.DOWN  ))
        	return true;
        if(meta == 1 && ( side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH ))
        	return true;
        if(meta == 2 && ( side == ForgeDirection.WEST  || side == ForgeDirection.EAST  ))
        	return true;
        return false;
    }
    
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    	return false;
    }
    
    public int getRenderType() {
        return CatwalkMod.supportRenderType;
    }
    
    public int onBlockPlaced(World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ, int itemMeta) {
    	ForgeDirection side = ForgeDirection.getOrientation(_side);
    	switch(side) {
    	case UP:
    	case DOWN:
    		return 0;
    	case NORTH:
    	case SOUTH:
    		return 1;
    	case EAST:
    	case WEST:
    		return 2;
    	default:
    		return 0;
    	}
    }
    
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, startVec, endVec);
    }
    
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersect, List list, Entity entity)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, intersect, list, entity);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
    	float d = 3/16F;
        float D = 1-d;
        float minX = 0, minY = 0, minZ = 0, maxX = 1, maxY = 1, maxZ = 1;

        if(meta == 0) { // up/down
    		minX= d; minY= 0; minZ= d;
    		maxX= D; maxY= 1; maxZ= D;
        }
        if(meta == 1) { // north/south
    		minX= d; minY= d; minZ= 0;
    		maxX= D; maxY= D; maxZ= 1;
        }
        if(meta == 2) { // east/west
        	minX= 0; minY= d; minZ= d;
    		maxX= 1; maxY= D; maxZ= D;
        }
        if(meta == 3) {
        	minX= d; minY= d; minZ= d;
        	maxX= D; maxY= D; maxZ= D;
        }
        
        if(world.getBlock(x-1, y, z) instanceof BlockSupportColumn) {
        	minX = 0;
        }
        if(world.getBlock(x, y-1, z) instanceof BlockSupportColumn) {
        	minY = 0;
        }
        if(world.getBlock(x, y, z-1) instanceof BlockSupportColumn) {
        	minZ = 0;
        }
        if(world.getBlock(x+1, y, z) instanceof BlockSupportColumn) {
        	maxX = 1;
        }
        if(world.getBlock(x, y+1, z) instanceof BlockSupportColumn) {
        	maxY = 1;
        }
        if(world.getBlock(x, y, z+1) instanceof BlockSupportColumn) {
        	maxZ = 1;
        }
        
        setBlockBounds(minX, minY, minZ,
        			   maxX, maxY, maxZ);
    }
    

    //==============================================================================
  	// Drop methods
  	//==============================================================================
  	
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z){
		int metadata = world.getBlockMetadata(x, y, z);
		float hardness = blockHardness;
		
		if(player.getHeldItem() != null ) {
			boolean shouldBeSoft = false;
			if(CatwalkUtil.isHoldingWrench(player))
				shouldBeSoft = true;
			
			if(shouldBeSoft)
				hardness = blockHardness/10;
		}

        return player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30F;
	}
    
  	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
  	{
  	    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
  	
  	    ret.add(new ItemStack(
  	    		Item.getItemFromBlock(CatwalkMod.supportColumn),
  	    		1));
  	    return ret;
  	}

  	public boolean canHarvestBlock(EntityPlayer player, int meta)
      {
          return true;
      }
    
    //==============================================================================
	// ICustomLadderVelocity
	//==============================================================================
    
	@Override
	public double getLadderVelocity(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return 0.05;
	}

	@Override
	public double getLadderFallVelocity(IBlockAccess world, int x, int y,
			int z, EntityLivingBase entity) {
		return -1;
	}

	@Override
	public boolean isOnLadder(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean shouldPlayStepSound(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity, boolean isMovingDown) {
		return !isMovingDown;
	}

	@Override
	public boolean shouldHoldOn(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return entity.isSneaking() || CatwalkUtil.isHoldingWrench(entity, false);
	}

	@Override
	public boolean shouldClimbDown(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return entity.isSneaking() && CatwalkUtil.isHoldingWrench(entity, false);
	}

	@Override
	public double getClimbDownVelocity(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return 0.03;
	}

	@Override
	public boolean shouldForceBackFaceRender() {
		return false;
	}

}
