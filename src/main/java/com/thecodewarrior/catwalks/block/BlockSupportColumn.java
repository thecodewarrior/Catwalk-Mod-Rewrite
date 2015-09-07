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
	
	IIcon side;
	IIcon top;
	
	IIcon inventory_top;
	IIcon inventory_side;
	
	public IIcon support_cross;
	
	public BlockSupportColumn() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabTransport);
		setBlockName("support_column");
		float d = 3/16F;
		setBlockBounds(   d, 0,   d,
						1-d, 1, 1-d);
		setHardness(1.0F);
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
			
//			if(item instanceof ItemBlock && ((ItemBlock)item).field_150939_a instanceof BlockSupportColumn &&
//					( side == ForgeDirection.UP || side == ForgeDirection.DOWN )) {
//				ItemBlock ib = (ItemBlock)item;
//				for(int i = 0; Math.abs(i) < 128; i -= side.offsetY) {
//					Block b = world.getBlock(x, y+i, z);
//					if(b.isReplaceable(world, x, y+i, z)){
//						//world.setBlock(x, y+i, z, ( (ItemBlock)item ).field_150939_a);
//						use = ib.placeBlockAt(player.getCurrentEquippedItem(), player, world,
//								x, y+i, z, side.getOpposite().ordinal(),
//								hitX, hitY+i, hitZ, player.getCurrentEquippedItem().getItemDamage());
//						
//						if(use) {
//							world.playSoundEffect(x+0.5, y+i+0.5, z+0.5, ib.field_150939_a.stepSound.func_150496_b(), (ib.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, ib.field_150939_a.stepSound.getPitch() * 0.8F);
//						}
//						break;
//					} else {
//						if( !(b instanceof BlockSupportColumn) ) {
//							break;
//						}
//					}
//				}
//				if(!use) {
//			        double d = 0.2; // random values will be between -d and +d
//			        double velocity = 0.07;
//					for(int i = 0; i < 10; i++) {
//						world.spawnParticle("smoke", x+0.5 + ( (Math.random()-0.5)* 2*d ), y+(side.offsetY > 0 ? 1 : -0.15)+ (Math.random()*d), z+0.5+( (Math.random()-0.5)* 2*d ),
//								0,0,0);
//					}
//				}
//				if(use && !player.capabilities.isCreativeMode)
//					player.getCurrentEquippedItem().stackSize--;
//				return true;
//			} else
			if(CatwalkUtil.isHoldingWrench(player) && player.isSneaking() && ( side == ForgeDirection.UP || side == ForgeDirection.DOWN)) {
				for(int i = 128*-side.offsetY; Math.abs(i) > 0; i += side.offsetY) {
					Block b = world.getBlock(x, y+i, z);
					if(b instanceof BlockSupportColumn){
						//world.setBlock(x, y+i, z, ( (ItemBlock)item ).field_150939_a);
						List<ItemStack> drops = b.getDrops(world, x, y+i, z, world.getBlockMetadata(x, y+i, z), 0);
						world.setBlockToAir(x, y+i, z);
						for(ItemStack s : drops) {
							CatwalkUtil.giveItemToPlayer(player, s);
						}
						break;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister reg) {
		this.side = reg.registerIcon(CatwalkMod.MODID + ":support_side");
		this.top  = reg.registerIcon(CatwalkMod.MODID + ":support_top");
        
        this.inventory_top  = reg.registerIcon(CatwalkMod.MODID + ":inventory/support_top");
        this.inventory_side = reg.registerIcon(CatwalkMod.MODID + ":inventory/support_side");
        
        this.support_cross = reg.registerIcon(CatwalkMod.MODID + ":support_cross");
	}
	
	@Override
	public IIcon getIcon(int _side, int meta) {
		
		if(_side >= 100) {
    		ForgeDirection side = ForgeDirection.getOrientation(_side - 100);
    		if(side == ForgeDirection.UP || side == ForgeDirection.DOWN)
            	return this.inventory_top;
            return this.inventory_side;
    	}
		
		ForgeDirection side = ForgeDirection.getOrientation(_side);
		
		if(side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
		    return top;
		}
		return this.side;
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
    	boolean render = true;
    	
//    	if(isSideEnd(world, x, y, z, si))
//    	
//    	if(ForgeDirection.UP.ordinal() == side && (
//    			world.isSideSolid(x, y, z, ForgeDirection.DOWN, false) ||
//    			world.getBlock(x, y, z) instanceof BlockSupportColumn
//    		)) {
//    		render = false;
//    	}
//    	if(ForgeDirection.DOWN.ordinal() == side && (
//    			world.isSideSolid(x, y, z, ForgeDirection.UP, false) ||
//    			world.getBlock(x, y, z) instanceof BlockSupportColumn
//    		)) {
//    		render = false;
//    	}
//    	
        return render;
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
        
        float minX = 0, minY = 0, minZ = 0, maxX = 1, maxY = 1, maxZ = 1;

        if(meta == 0) { // up/down
    		minX= d;   minY= 0; minZ= d;
    		maxX= 1-d; maxY= 1; maxZ= 1-d;
        }
        if(meta == 1) { // north/south
    		minX= d;   minY= d;   minZ= 0;
    		maxX= 1-d; maxY= 1-d; maxZ= 1;
        }
        if(meta == 2) { // east/west
        	minX= 0; minY= d;   minZ= d;
    		maxX= 1; maxY= 1-d; maxZ= 1-d;
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
