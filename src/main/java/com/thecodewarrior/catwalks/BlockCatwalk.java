package com.thecodewarrior.catwalks;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import buildcraft.api.tools.IToolWrench;
import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCatwalk extends Block {
	public boolean lights;
	public boolean bottom;
	
	private RayTracer rayTracer = new RayTracer();
	
	
	public IIcon transparent;

	public IIcon sideTexture;
	public IIcon bottomTexture;
	
	public IIcon bottomTextureWithLights;
	public IIcon sideTextureWithLights;
	
	public IIcon bottomLights;
	public IIcon sideLights;
		
	public BlockCatwalk(boolean lights, boolean bottom) {
		super(Material.iron);
		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		setBlockName("catwalk");
		if(lights && !bottom)
			setCreativeTab(CreativeTabs.tabTransport);
//		setHarvestLevel("wrench", 0);
//		setHarvestLevel("pickaxe", 0);
		this.lights = lights;
		this.bottom = bottom;
	}
	
	//==============================================================================
	// Clicking methods
	//==============================================================================
	
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z){
		int metadata = world.getBlockMetadata(x, y, z);
		float hardness = blockHardness;
		
		if(player.getHeldItem() != null ) {
			boolean shouldBeSoft = false;
			if(player.getHeldItem().getItem() instanceof IToolWrench)
				shouldBeSoft = true;
			Set<String> toolClasses = player.getHeldItem().getItem().getToolClasses(player.getHeldItem());
			if(toolClasses.contains("wrench"))
				shouldBeSoft = true;
			
			if(shouldBeSoft)
				hardness = blockHardness/10;
		}

        return player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30F;
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
		
		ForgeDirection side = ForgeDirection.UP;
		if (hit != null) {
			side = (ForgeDirection) ( (ExtendedMOP) hit ).data;
		}
		
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
			if(player.isSneaking()) {

				this.dropBlockAsItem(world, x, y, z, 0, 0);
				world.setBlockToAir(x,y,z);
				this.updateNeighborSides(world, x, y, z, false);
			}
		}
	}

	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {
		MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
		
		ForgeDirection side = ForgeDirection.UP;
		if (hit != null) {
			side = (ForgeDirection) ( (ExtendedMOP) hit ).data;
		}
		
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
			if(player.isSneaking()) {
				if(this.lights) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(new EntityItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(CatwalkMod.itemRopeLight, 1)));
						updateData(world, x, y, z, ForgeDirection.UP, false, false);
					}
				}
			} else {
				if(side != ForgeDirection.UP) {
					updateData(world,x,y,z, side, !getOpenState(world,x,y,z, side), this.lights);
				}
			}
		}
		
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemRopeLight && this.lights == false) {
			updateData(world, x, y, z, ForgeDirection.UP, false, true);
			if(!player.capabilities.isCreativeMode)
				player.getCurrentEquippedItem().stackSize--;
		}
//		
//		if()
		
		return false;
	}
	
	//==============================================================================
	// Place/Destroy methods
	//==============================================================================
	
    @Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase e, ItemStack s) {
		updateNeighborSides(w,x,y,z,true);
	}

	public void onBlockDestroyedByPlayer(World w, int x, int y, int z, int meta) {
		updateNeighborSides(w,x,y,z,false);
	}

	public void onBlockDestroyedByExplosion(World w, int x, int y, int z, Explosion e) {
		updateNeighborSides(w,x,y,z,false);
	}

	//==============================================================================
	// Drop methods
	//==============================================================================
	
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
	    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
	
	    ret.add(new ItemStack(
	    		Item.getItemFromBlock(CatwalkMod.catwalkUnlitBottom),
	    		1));
	    if(this.lights) {
	    	ret.add(new ItemStack(
	    			CatwalkMod.itemRopeLight,
	    			1
	    		));
	    }
	    return ret;
	}

	public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return true;
    }
	
	//==============================================================================
	// Block highlight raytrace methods
	//==============================================================================
	
	@Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        
        float px = 1/16F;
    	int meta = world.getBlockMetadata(x, y, z);
//    	System.out.println("META: " + (meta & 8));
    	boolean ovr = false;
    	double d = 0;
    	float smallHeight = 0.25f;
    	if(bottom) {
    		d = 0.125;
    		//cuboids.add(new IndexedCuboid6(Hitboxes.BOTTOM, new Cuboid6(x+ px, y+ 0, z+ px, x+ 1-px, y+ px, z+ 1-px)));
    	} else {
    		d = 0.25;
    	}
    	cuboids.add(new IndexedCuboid6(ForgeDirection.DOWN, new Cuboid6(x+ d, y+ 0, z+ d, x+ 1-d, y+ px, z+ 1-d)));
    	float ym = 1;
    	
    	if((meta & 8) == 0 || ovr) { ym = 1; } else { ym = smallHeight; }
    	cuboids.add(new IndexedCuboid6(ForgeDirection.NORTH, new Cuboid6(
				x+ 0, 	y+ 0, 	z+ 0,
				x+ 1, 	y+ ym, 	z+ px
			)));
    	
    	
    	if((meta & 4) == 0 || ovr) { ym = 1; } else { ym = smallHeight; }
    	cuboids.add(new IndexedCuboid6(ForgeDirection.SOUTH, new Cuboid6(
				x+ 0, 	y+ 0, 	z+ 1-px,
				x+ 1, 	y+ ym, 	z+ 1
			)));
    	
    	
    	if((meta & 2) == 0 || ovr) { ym = 1; } else { ym = smallHeight; }
    	cuboids.add(new IndexedCuboid6(ForgeDirection.WEST, new Cuboid6(
				x+ 0, 	y+ 0, 	z+ 0,
				x+ px, 	y+ ym, 	z+ 1
			)));
    	
    	
    	if((meta & 1) == 0 || ovr) { ym = 1; } else { ym = smallHeight; }
    	cuboids.add(new IndexedCuboid6(ForgeDirection.EAST, new Cuboid6(
				x+ 1-px, y+ 0, 	z+ 0,
				x+ 1, 	 y+ ym, 	z+ 1
			)));
    	//new BlockCoord(x, y, z), this
        ExtendedMOP mop = (ExtendedMOP) rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(x, y, z), this);
        if(mop != null) {
        	if(mop.sideHit == ((ForgeDirection)mop.data).getOpposite().ordinal()) {
        		mop.sideHit = ((ForgeDirection)mop.data).ordinal();
        	
        	}
        }
        //        System.out.println(mop.data);
        return mop;
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.target.typeOfHit == MovingObjectType.BLOCK && event.player.worldObj.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ) == this)
            RayTracer.retraceBlock(event.player.worldObj, event.player, event.target.blockX, event.target.blockY, event.target.blockZ);
    }
    
    //==============================================================================
	// Collision methods
	//==============================================================================
		
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
			return;
		double mul = 1.315;
		if(entity instanceof EntityPlayer && ( (EntityPlayer)entity).isSneaking() ) {
			mul = 2.860578;
		}
		CatwalkMod.proxy.speedupPlayer(world, entity, mul);
	}

	@Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB blockBounds, List list, Entity collidingEntity) {
    	
		if(collidingEntity == null)
			return;
	
		float px = 1/16F;
    	int meta = world.getBlockMetadata(x, y, z);
    	float top = 1.5F;
    	if(collidingEntity.isSneaking())
    		top = 1.0F;
    	
    	boolean ovr = false;
    	Cuboid6 oldBounds = new Cuboid6(
    			this.getBlockBoundsMinX(), this.getBlockBoundsMinY(), this.getBlockBoundsMinZ(),
    			this.getBlockBoundsMaxX(), this.getBlockBoundsMaxY(), this.getBlockBoundsMaxZ()
    		);
    	if(bottom) {
    		this.setBlockBounds(0, 0, 0, 1, px, 1);
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if((meta & 8) == 0 || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.NORTH)) {
    			this.setBlockBounds(0, 0, 0, 1, 1, px);
    		} else {
    			this.setBlockBounds(0, 0, 0, 1, top, px);
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if((meta & 4) == 0 || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.SOUTH)) {
    			this.setBlockBounds(0, 0, 1-px, 1, 1, 1);
    		} else {
    			this.setBlockBounds(0, 0, 1-px, 1, top, 1);
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if((meta & 2) == 0 || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.WEST)) {
    			this.setBlockBounds(0, 0, 0, px, 1, 1);
    		} else {
    			this.setBlockBounds(0, 0, 0, px, top, 1);
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if((meta & 1) == 0 || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.EAST)) {
    			this.setBlockBounds(1-px, 0, 0, 1, 1, 1);
    		} else {
    			this.setBlockBounds(1-px, 0, 0, 1, top, 1);    			
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	
    	oldBounds.setBlockBounds(this);
//    	this.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

	public boolean getStepConnect(World w, int x, int y, int z, ForgeDirection direction) {
		int newX = x+direction.offsetX,
			newY = y+1,
			newZ = z+direction.offsetZ;
		
		Block rawBlock = w.getBlock(newX, newY, newZ);
		int meta = w.getBlockMetadata(newX, newY, newZ);
		if(rawBlock instanceof BlockCatwalk) {
			BlockCatwalk b = (BlockCatwalk) rawBlock;
			return b.getOpenState(w, newX, newY, newZ, direction.getOpposite());
		}
		
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
	{
	    return false;
	}

	//==============================================================================
	// Texture methods
	//==============================================================================
	
	@Override
	public void registerBlockIcons(IIconRegister reg) {
		transparent   			= reg.registerIcon("catwalks:transparent");
		
		sideTexture   			= reg.registerIcon("catwalks:side");
		bottomTexture 			= reg.registerIcon("catwalks:bottom");
		
	    sideTextureWithLights   = reg.registerIcon("catwalks:side_with_lights");
	    bottomTextureWithLights = reg.registerIcon("catwalks:bottom_with_lights");
	    
	    sideLights   			= reg.registerIcon("catwalks:side_lights");
	    bottomLights 			= reg.registerIcon("catwalks:bottom_lights");
	}

	@Override
	public IIcon getIcon(int _side, int meta) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[_side];
		/**/
	    if(dir == ForgeDirection.DOWN) {
			if(bottom) {
				return lights ? bottomTextureWithLights : bottomTexture;
			}
	    }
	    if(dir == ForgeDirection.NORTH && (meta & 8) < 1) {
	    	return lights ? sideTextureWithLights : sideTexture;
	    }
	    if(dir == ForgeDirection.SOUTH && (meta & 4) < 1) {
	    	return lights ? sideTextureWithLights : sideTexture;
	    }
	    if(dir == ForgeDirection.WEST && (meta & 2) < 1) {
	    	return lights ? sideTextureWithLights : sideTexture;
	    }
	    if(dir == ForgeDirection.EAST && (meta & 1) < 1) {
	    	return lights ? sideTextureWithLights : sideTexture;
	    }
	    return transparent; /**/
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
		return !getOpenState(w,x,y,z,side);//getOpenState(w, x-side.offsetX, y-side.offsetY, z-side.offsetZ, side.getOpposite());
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int _side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(_side);
	    int meta = w.getBlockMetadata(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
	
	    if(dir == ForgeDirection.DOWN && (
	    		bottom == false ||
	    		( w.isSideSolid(x, y, z, ForgeDirection.UP, false) && !(w.getBlock(x,y,z) instanceof BlockCatwalk) )
	    	)) {
	    	return false;
	    }
	    if(dir == ForgeDirection.NORTH && (
	    		(meta & 8) > 0 ||
	    		( w.isSideSolid(x, y, z, ForgeDirection.SOUTH, false) && !(w.getBlock(x,y,z) instanceof BlockCatwalk) )
	    	)) {
	    	return false;
	    }
	    if(dir == ForgeDirection.SOUTH && (
	    		(meta & 4) > 0 ||
	    		( w.isSideSolid(x, y, z, ForgeDirection.NORTH, false) )// && !(w.getBlock(x,y,z) instanceof BlockCatwalk) )
	    	)) {
	    	return false;
	    }
	    if(dir == ForgeDirection.WEST && (
	    		(meta & 2) > 0 ||
	    		( w.isSideSolid(x, y, z, ForgeDirection.EAST, false) && !(w.getBlock(x,y,z) instanceof BlockCatwalk) )
	    	)) {
	    	return false;
	    }
	    if(dir == ForgeDirection.EAST && (
	    		(meta & 1) > 0 ||
	    		( w.isSideSolid(x, y, z, ForgeDirection.WEST, false) )// && !(w.getBlock(x,y,z) instanceof BlockCatwalk) )
	    	)) {
	    	return false;
	    }
	    return true;
	}
	
	

	/**
	 * Gets the light value of the specified block coords. Args: x, y, z
	 */
	public int getLightValue()
	{
	    return this.lights ? 15 : 0;
	}

	//==============================================================================
	// Data update methods
	//==============================================================================
	
	public void updateNeighborSides(World w, int x, int y, int z, boolean updateSelf) {
    	for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int newX = x + dir.offsetX;
			int newY = y + dir.offsetY;
			int newZ = z + dir.offsetZ;
			Block b  = w.getBlock(newX,newY,newZ);
			if(b instanceof BlockCatwalk)
				((BlockCatwalk) b).updateOpenStatus(w,newX,newY,newZ, dir.getOpposite());
			
		}
    	if(updateSelf) {
	    	for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
	    		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
	    		this.updateOpenStatus(w, x, y, z, dir);
	    	}
    		this.updateOpenStatus(w, x, y, z, ForgeDirection.DOWN);
    	}
    }
    
    public void updateOpenStatus(World w, int x, int y, int z, ForgeDirection side) {
		updateData(w,x,y,z,side, shouldBeOpen(w,x,y,z,side), this.lights);
	}

	public void updateData(World w, int x, int y, int z, ForgeDirection side, boolean state, boolean lights) {
		int meta = w.getBlockMetadata(x, y, z);
		if(side == ForgeDirection.NORTH) {
			if(state) {
				meta = meta | 8;  // 0b1000
			} else {
				meta = meta & 7; // 0b0111
			}
		}
		if(side == ForgeDirection.SOUTH) {
			if(state) {
				meta = meta | 4;  // 0b0100
			} else {
				meta = meta & 11; // 0b1011
			}
		}
		if(side == ForgeDirection.WEST) {
			if(state) {
				meta = meta | 2;  // 0b0010
			} else {
				meta = meta & 13; // 0b1101
			}
		}
		if(side == ForgeDirection.EAST) {
			if(state) {
				meta = meta | 1;  // 0b0001
			} else {
				meta = meta & 14; // 0b1110
			}
		}
		Block block = this;
		if(side == ForgeDirection.DOWN) {
			if(state) {
				if(lights) {
					block = CatwalkMod.catwalkLitNoBottom;
				} else {
					block = CatwalkMod.catwalkUnlitNoBottom;
				}
			} else {
				if(lights) {
					block = CatwalkMod.catwalkLitBottom;
				} else {
					block = CatwalkMod.catwalkUnlitBottom;
				}
			}
		} else {
			if(!bottom) {
				if(lights) {
					block = CatwalkMod.catwalkLitNoBottom;
				} else {
					block = CatwalkMod.catwalkUnlitNoBottom;
				}
			} else {
				if(lights) {
					block = CatwalkMod.catwalkLitBottom;
				} else {
					block = CatwalkMod.catwalkUnlitBottom;
				}
			}
		}
		w.setBlock(x, y, z, block, meta, 3);
	}

	public boolean shouldBeOpen(World w, int x, int y, int z, ForgeDirection direction) {
		int newX = x + direction.offsetX;
		int newY = y + direction.offsetY;
		int newZ = z + direction.offsetZ;
		if(w.getBlock(newX,newY,newZ) instanceof BlockCatwalk)
			return true;
		return false;
	}

	public boolean getOpenState(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
		int meta = w.getBlockMetadata(x, y, z);
	
	    if(side == ForgeDirection.DOWN && bottom == false) {
	    	return true;
	    }
	    if(side == ForgeDirection.NORTH && (meta & 8) > 0) {
	    	return true;
	    }
	    if(side == ForgeDirection.SOUTH && (meta & 4) > 0) {
	    	return true;
	    }
	    if(side == ForgeDirection.WEST && (meta & 2) > 0) {
	    	return true;
	    }
	    if(side == ForgeDirection.EAST && (meta & 1) > 0) {
	    	return true;
	    }
	    return false;
	}

	//==============================================================================
	// Render type methods
	//==============================================================================
	
	public int getRenderType(){
	    return CatwalkMod.catwalkRenderType;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
	    return 0;
	}

	public boolean isOpaqueCube()
	{
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
}
