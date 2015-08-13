package com.thecodewarrior.catwalks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thecodewarrior.catwalks.BlockCagedLadder.RelativeSide;
import com.thecodewarrior.catwalks.BlockCagedLadder.TextureSide;
import com.thecodewarrior.catwalks.BlockCagedLadder.TextureType;
import com.thecodewarrior.codechicken.lib.raytracer.ExtendedMOP;
import com.thecodewarrior.codechicken.lib.raytracer.IndexedCuboid6;
import com.thecodewarrior.codechicken.lib.raytracer.RayTracer;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;
import com.thecodewarrior.codechicken.lib.vec.Cuboid6;
import com.thecodewarrior.codechicken.lib.vec.Vector3;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCatwalk extends Block implements ICagedLadderConnectable {
	public boolean lights;
	public boolean bottom;
	public boolean tape;
	
	private RayTracer rayTracer = new RayTracer();
	
	
	public IIcon transparent;

//	public IIcon sideTexture;
//	public IIcon bottomTexture;
//	
//	public IIcon bottomTextureWithLights;
//	public IIcon sideTextureWithLights;
//	
//	public IIcon bottomLights;
//	public IIcon sideLights;
		
public Map<TextureSide, Map<TextureType, IIcon>> textures;
	
	public enum TextureSide {
		BOTTOM("bottom"),
		SIDE("side");
		
		public String filename;
		private TextureSide(String filename) {
			this.filename = filename;
		}
		
		public static TextureSide fromFD(ForgeDirection side) {
			switch(side) {
			case DOWN:
				return BOTTOM;
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				return SIDE;
			default:
				return BOTTOM;
			}
		}
		
	}
	public enum TextureType {
		LIGHTS("plain/lights"),
		T_LIGHTS("tape/lights"),
		
		W_LIGHTS("plain/w_lights"),
		WO_LIGHTS("plain/no_lights"),
		
		T_W_LIGHTS("tape/w_lights"),
		T_WO_LIGHTS("tape/no_lights");
		
		public String filename;
		private TextureType(String filename) {
			this.filename = filename;
		}
		public static TextureType fromLightsAndTape(boolean lights, boolean tape) {
			if(!lights && !tape) return WO_LIGHTS;
			if( lights && !tape) return W_LIGHTS;
			if(!lights &&  tape) return T_WO_LIGHTS;
			if( lights &&  tape) return T_W_LIGHTS;

			return WO_LIGHTS;
		}
	}
	
	public BlockCatwalk(boolean lights, boolean bottom, boolean tape) {
		super(Material.iron);
		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		setBlockName("catwalk");
		if(!lights && !bottom && !tape)
			setCreativeTab(CreativeTabs.tabTransport);
		this.lights = lights;
		this.bottom = bottom;
		this.tape   = tape;
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
			if(side != ForgeDirection.UP) {
				updateData(world,x,y,z, side, !getOpenState(world,x,y,z, side), this.lights, this.tape);
			}
		}
		
		if(player.getCurrentEquippedItem() != null) {
			Item item = player.getCurrentEquippedItem().getItem();
			boolean use = false;
			
			if(item instanceof ItemRopeLight && this.lights == false) {
				updateData(world, x, y, z, ForgeDirection.UP, false, true, this.tape);
				use = true;
			}
			
			if(item instanceof ItemCautionTape && this.tape == false) {
				updateData(world, x, y, z, ForgeDirection.UP, false, this.lights, true);
				use = true;
			}
			
			if(use && !player.capabilities.isCreativeMode)
				player.getCurrentEquippedItem().stackSize--;
		}
		
		if(player.getCurrentEquippedItem() == null && player.isSneaking()) {
			if(this.lights) {
				if(!world.isRemote) {
					world.spawnEntityInWorld(new EntityItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(CatwalkMod.itemRopeLight, 1)));
					updateData(world, x, y, z, ForgeDirection.UP, false, false, this.tape);
				}
			} else if(this.tape) {
				if(!world.isRemote) {
					world.spawnEntityInWorld(new EntityItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(CatwalkMod.itemCautionTape, 1)));
					updateData(world, x, y, z, ForgeDirection.UP, false, this.lights, false);
				}
			}
		}
		
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

	public boolean shouldHaveBox(World world, int x, int y, int z, ForgeDirection side) {
		return  !(world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ) instanceof BlockCatwalk) &&
				!world.isSideSolid(x+side.offsetX, y+side.offsetY, z+side.offsetZ, side.getOpposite(), false);
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
    	if(bottom && shouldHaveBox(world, x, y, z, ForgeDirection.DOWN)) {
    		this.setBlockBounds(0, 0, 0, 1, px, 1);
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	
    	if(( !getBit(meta, 3) && shouldHaveBox(world, x, y, z, ForgeDirection.NORTH) ) || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.NORTH)) {
    			this.setBlockBounds(0, 0, 0, 1, 1, px);
    		} else {
    			this.setBlockBounds(0, 0, 0, 1, top, px);
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if(( !getBit(meta, 2) && shouldHaveBox(world, x, y, z, ForgeDirection.SOUTH) ) || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.SOUTH)) {
    			this.setBlockBounds(0, 0, 1-px, 1, 1, 1);
    		} else {
    			this.setBlockBounds(0, 0, 1-px, 1, top, 1);
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if(( !getBit(meta, 1) && shouldHaveBox(world, x, y, z, ForgeDirection.WEST) ) || ovr) {
    		if(getStepConnect(world,x,y,z,ForgeDirection.WEST)) {
    			this.setBlockBounds(0, 0, 0, px, 1, 1);
    		} else {
    			this.setBlockBounds(0, 0, 0, px, top, 1);
    		}
            super.addCollisionBoxesToList(world, x, y, z, blockBounds, list, collidingEntity);
    	}
    	if(( !getBit(meta, 0) && shouldHaveBox(world, x, y, z, ForgeDirection.EAST) ) || ovr) {
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
		
		textures = new HashMap<TextureSide, Map<TextureType,IIcon>>();
	    for (TextureSide side : TextureSide.values()) {
	    	Map<TextureType, IIcon> sideMap = new HashMap<TextureType, IIcon>();
	    	textures.put(side, sideMap);
	    	for (TextureType type : TextureType.values()) {
				IIcon icon = reg.registerIcon("catwalks:catwalk/" + side.filename + "/" + type.filename);
				
				sideMap.put(type, icon);
			}
		}
		
	}
	
	@Override
	public IIcon getIcon(int _side, int meta) {
		ForgeDirection side = ForgeDirection.getOrientation(_side);
		
		if(side == ForgeDirection.UP) {
		    return transparent;
		}
		
		TextureSide tSide = TextureSide.fromFD(side);
		TextureType type = TextureType.fromLightsAndTape(lights, tape);
		
		return textures.get(tSide).get(type);
	}
	
	public IIcon getLightIcon(int _side, int meta) {
		ForgeDirection side = ForgeDirection.getOrientation(_side);
		
		if(side == ForgeDirection.UP) {
		    return transparent;
		}
		TextureSide tSide = TextureSide.fromFD(side);
		
		return textures.get(tSide).get(tape ? TextureType.T_LIGHTS : TextureType.LIGHTS);
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
	
	public int setBit(int val, int pos, boolean value) {
		if(value)
			return val |  (1 << pos);
		else
			return val & ~(1 << pos);
	}

	public boolean getBit(int val, int pos) {
		return ( val & (1 << pos) ) > 0;
	}
	
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
		updateData(w,x,y,z,side, shouldBeOpen(w,x,y,z,side), this.lights, this.tape);
	}

	public void updateData(World w, int x, int y, int z, ForgeDirection side, boolean state, boolean lights, boolean tape) {
		int meta = w.getBlockMetadata(x, y, z);
		if(side == ForgeDirection.NORTH)
			meta = setBit(meta, 3, state);
		if(side == ForgeDirection.SOUTH)
			meta = setBit(meta, 2, state);
		if(side == ForgeDirection.WEST)
			meta = setBit(meta, 1, state);
		if(side == ForgeDirection.EAST)
			meta = setBit(meta, 0, state);
		
		boolean bottom = this.bottom;
		if(side == ForgeDirection.DOWN)
			bottom = !state; // don't know—or have the energy to figure out—why this needs to be inverted. It just works.
		
		Block block = CatwalkMod.catwalks.get(lights).get(bottom).get(tape);
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
	
	

	//==============================================================================
	// ICagedLadderConnectable
	//==============================================================================
	
	@Override
	public boolean shouldConnectToSide(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		return getOpenState(w, x, y, z, side);
	}

	@Override
	public boolean shouldHaveBottom(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		if(bottom)
			return true;
		Block b = w.getBlock(x,y-1,z);
		if( b instanceof ICagedLadderConnectable) {
			if( ((ICagedLadderConnectable)b).doesSideHaveWall(w, x, y, z, side) )
				return true;
		}
		return false;
	}
	
	@Override
	public boolean doesSideHaveWall(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		return !getOpenState(w, x, y, z, side);
	}
	
	@Override
	public boolean isThin(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
		return false;
	}
}
