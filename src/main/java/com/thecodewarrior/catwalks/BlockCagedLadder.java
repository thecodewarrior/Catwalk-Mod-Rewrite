package com.thecodewarrior.catwalks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCagedLadder extends Block implements ICustomLadderVelocity, ICagedLadderConnectable {

	RayTracer rayTracer = new RayTracer();
	
	public ForgeDirection direction;
	public boolean lights;
	
	public IIcon bottom;
	public IIcon bottom_with_lights;
	public IIcon bottom_lights;
	
	public IIcon ladder;
	public IIcon ladder_with_lights;
	public IIcon ladder_lights;
	
	public IIcon front;
	public IIcon front_with_lights;
	public IIcon front_lights;
	
	public IIcon side;
	public IIcon side_with_lights;
	public IIcon side_lights;
	
	public IIcon landing;
	public IIcon landing_side;
	
	public IIcon transparent;
	
	public Map<RelativeSide, Cuboid6> closed = new HashMap<RelativeSide, Cuboid6>();
	public Map<RelativeSide, Cuboid6>   open = new HashMap<RelativeSide, Cuboid6>();
	
	// meta: bottom, front, left, right
	
	public BlockCagedLadder(ForgeDirection direction, boolean lights) {
		super(Material.iron);
		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		float px = 1/16F;
    	setBlockBounds(px, 0, px, 1-px, 1, 1-px);
		setBlockName("cagedladder");
		setStepSound(soundTypeLadder);
		if(direction == ForgeDirection.NORTH && !lights)
			setCreativeTab(CreativeTabs.tabTransport);
		setHarvestLevel("wrench", 0);
		setHarvestLevel("pickaxe", 0);
		this.lights = lights;
		this.direction = direction;
		initHitBoxes();
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
	
	public void updateNeighborSides(World world, int x, int y, int z, boolean self) {
		Block b = world.getBlock(x, y+1, z);
		if(b instanceof BlockCagedLadder) {
//			this.updateOpenData(world, x, y+1, z, RelativeSide.BOTTOM, false);
//			System.out.println("---");
			( (BlockCagedLadder)b ).updateBottom(world, x, y+1, z);
//			System.out.println("---");
		}
		if(self)
			this.updateBottom(world, x, y, z);
	}
	
	public void updateBottom(World world, int x, int y, int z) {
//		System.out.println("UPD");
		if(world.getBlock(x, y-1, z) instanceof BlockCagedLadder) {
			updateOpenData(world, x, y, z, RelativeSide.BOTTOM, true);
		} else {
			updateOpenData(world, x, y, z, RelativeSide.BOTTOM, false);
		}
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
		
		RelativeSide side = RelativeSide.TOP;
		if (hit != null) {
			side = (RelativeSide) ( (ExtendedMOP) hit ).data;
		}
		
//		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
//			if(player.isSneaking()) {
//
//				this.dropBlockAsItem(world, x, y, z, 0, 0);
//				world.setBlockToAir(x,y,z);
//				this.updateNeighborSides(world, x, y, z, false);
//			}
//		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {
		MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
		
		RelativeSide side = RelativeSide.TOP;
		if (hit != null) {
			side = (RelativeSide) ( (ExtendedMOP) hit ).data;
		}
//		System.out.println(side);
		
		ItemStack handStack = player.getCurrentEquippedItem();
		if(handStack != null && handStack.getItem() instanceof IToolWrench) {
			if(player.isSneaking()) {
				if(this.lights) {
					if(!world.isRemote) {
						world.spawnEntityInWorld(new EntityItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(CatwalkMod.itemRopeLight, 1)));
						updateIdData(world, x, y, z, direction, false);
					}
				}
			} else {
//				System.out.println("META:  " + world.getBlockMetadata(x,y,z));
				updateOpenData(world, x, y, z, side, !isOpen(side, world.getBlockMetadata(x, y, z)));
//				System.out.println("META2: " + world.getBlockMetadata(x,y,z));
			}
		}
		
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemRopeLight && this.lights == false) {
			updateIdData(world, x, y, z, direction, true);
			if(!player.capabilities.isCreativeMode)
				player.getCurrentEquippedItem().stackSize--;
		}
		
		return false;
	}
	
	//==============================================================================
	// Ladder methods
	//==============================================================================
	
	@Override
    public boolean isOnLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
		float px = 1/16F;
		float px2 = 2*px;
		float d = px*3;
        return CatwalkMod.options.fullBlockLadder || entity.boundingBox.intersectsWith(AxisAlignedBB.getBoundingBox(x+d, y, z+d, x+1-d, y+1, z+1-d));
    }
	
	public double getLadderVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return entity.isSneaking() ? 0.15D : 0.25D;
	}
	
	public double getLadderFallVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return 0.25D;
	}
	

	//==============================================================================
	// Block highlight raytrace methods
	//==============================================================================
	
	public void initHitBoxes() {		
		double in = 1/32D;
		double out  = 3/32D;
		
		closed.put(RelativeSide.FDtoRS(ForgeDirection.NORTH, direction), new Cuboid6(
				in,	  0, in,
				1-in, 1, out
				)
		);
		
		closed.put(RelativeSide.FDtoRS(ForgeDirection.SOUTH, direction), new Cuboid6(
				in,	  0, 1-out,
				1-in, 1, 1-in
				)
		);
		
		closed.put(RelativeSide.FDtoRS(ForgeDirection.WEST, direction), new Cuboid6(
				in,  0, in,
				out, 1, 1-in
				)
		);
		
		closed.put(RelativeSide.FDtoRS(ForgeDirection.EAST, direction), new Cuboid6(
				1-in,  0, in,
				1-out, 1, 1-in
				)
		);
		
		closed.put(RelativeSide.FDtoRS(ForgeDirection.DOWN, direction), new Cuboid6(
				in,   0,   in,
				1-in, out, 1-in
				)
		);
		
		closed.put(RelativeSide.FDtoRS(ForgeDirection.UP, direction), new Cuboid6(
				in,   1-out,   in,
				1-in, 1, 1-in
				)
		);
		
		open.put(RelativeSide.FRONT, getOpenHighlightCuboid(open, RelativeSide.FRONT, direction) );
		open.put(RelativeSide.LEFT, getOpenHighlightCuboid(open, RelativeSide.LEFT, direction) );
		open.put(RelativeSide.RIGHT, getOpenHighlightCuboid(open, RelativeSide.RIGHT, direction) );
		open.put(RelativeSide.BOTTOM, getOpenHighlightCuboid(open, RelativeSide.BOTTOM, direction) );
		open.put(RelativeSide.TOP, getOpenHighlightCuboid(open, RelativeSide.TOP, direction) );
	}

	public Cuboid6 getOpenHighlightCuboid(Map<RelativeSide, Cuboid6> map, RelativeSide side, ForgeDirection facing) {
//		double width = 0.25D;
		double sub = 11/16D;//1-(1/16D)-width;
		double frontSub = sub/2;
		
		Cuboid6 cuboid = closed.get(side).copy();
		if(side == RelativeSide.LADDER)
			return cuboid;
			
		if(side == RelativeSide.TOP) {
			cuboid.min.x += frontSub;
			cuboid.min.z += frontSub;

			cuboid.max.x -= frontSub;
			cuboid.max.z -= frontSub;
		} else if(side != RelativeSide.FRONT) {
			if(facing.offsetX < 0) {
				cuboid.min.x += sub;
			}
			if(facing.offsetX > 0) {
				cuboid.max.x -= sub;
			}
			
			if(facing.offsetZ < 0) {
				cuboid.max.z -= sub;
			}
			if(facing.offsetZ > 0) {
				cuboid.min.z += sub;
			}
		} else {
			if(facing.offsetX != 0) {
				cuboid.min.z += frontSub;
				cuboid.max.z -= frontSub;
			}
			if(facing.offsetZ != 0) {
				cuboid.min.x += frontSub;
				cuboid.max.x -= frontSub;
			}
		}
		return cuboid;
	}

	@Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        
    	float ym = 1;
    	
    	float px = 1/16F;
		float px2 = 2*px;

		float outsideDistance = 1/32F;
		float insideDistance  = 3/32F;
		
		float out = px/2;
		float in = out+px;
    	
		int meta = world.getBlockMetadata(x, y, z);
		
		if(!(world.getBlock(x, y+1, z) instanceof BlockCagedLadder)) {
			addToList(x, y, z, cuboids, RelativeSide.TOP, open.get(RelativeSide.TOP));
		}
		
    	for (RelativeSide rs : RelativeSide.values()) {
			if(rs == RelativeSide.LADDER) {
				addToList(x, y, z, cuboids, rs, closed.get(rs));
				continue;
			}
			if(rs == RelativeSide.TOP) {
				continue;
			}
			if(isOpen(rs, meta)) {
				addToList(x, y, z, cuboids, rs, open.get(rs));
			} else {
				addToList(x, y, z, cuboids, rs, closed.get(rs));
			}
		}
    	
        ExtendedMOP mop = (ExtendedMOP) rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(x, y, z), this);
        if(mop != null) {
        	if(mop.sideHit == RelativeSide.RStoFD( (RelativeSide)mop.data, direction).getOpposite().ordinal()) {
        		mop.sideHit = ForgeDirection.getOrientation(mop.sideHit).getOpposite().ordinal();
        	}
        }
        //        System.out.println(mop.data);
        return mop;
    }
    
	public void addToList(int x, int y, int z, List<IndexedCuboid6> list, Object data, Cuboid6 cuboid) {

		list.add( new IndexedCuboid6(data, new Cuboid6(
				x+cuboid.min.x, y+cuboid.min.y, z+cuboid.min.z,
				x+cuboid.max.x, y+cuboid.max.y, z+cuboid.max.z
				)) );
	}
	
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.target.typeOfHit == MovingObjectType.BLOCK && event.player.worldObj.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ) == this)
            RayTracer.retraceBlock(event.player.worldObj, event.player, event.target.blockX, event.target.blockY, event.target.blockZ);
    }
	
	//==============================================================================
	// Texture methods
	//==============================================================================
	
	@Override
	public void registerBlockIcons(IIconRegister reg) {
	    transparent   		= reg.registerIcon("catwalks:transparent");
	    
	    bottom 				= reg.registerIcon("catwalks:ladder/bottom");
	    bottom_with_lights	= reg.registerIcon("catwalks:ladder/bottom_with_lights");
	    bottom_lights		= reg.registerIcon("catwalks:ladder/bottom_lights");
	    
	    ladder 				= reg.registerIcon("catwalks:ladder/ladder");
		ladder_with_lights 	= reg.registerIcon("catwalks:ladder/ladder_with_lights");
		ladder_lights 		= reg.registerIcon("catwalks:ladder/ladder_lights");
		
		front 				= reg.registerIcon("catwalks:ladder/front");
		front_with_lights 	= reg.registerIcon("catwalks:ladder/front_with_lights");
		front_lights 		= reg.registerIcon("catwalks:ladder/front_lights");
		
		side 				= reg.registerIcon("catwalks:ladder/side");
		side_with_lights 	= reg.registerIcon("catwalks:ladder/side_with_lights");
		side_lights 		= reg.registerIcon("catwalks:ladder/side_lights");
		
		landing 			= reg.registerIcon("catwalks:ladder/landing");
		landing_side		= reg.registerIcon("catwalks:ladder/landing_side");
	}
	
	@Override
	public IIcon getIcon(int _side, int meta) {
		RelativeSide dir = RelativeSide.FDtoRS( ForgeDirection.getOrientation(_side), direction );
		
		switch(dir) {
		case LADDER:
			if(lights)
				return ladder_with_lights;
			else
				return ladder;
		case FRONT:
			if(lights)
				return front_with_lights;
			else
				return front;
		case LEFT:
		case RIGHT:
			if(lights)
				return side_with_lights;
			else
				return side;
		case BOTTOM:
			if(lights)
				return bottom_with_lights;
			else
				return bottom;
		}
		
	    return transparent;
	}
	
	public IIcon getLightIcon(int _side, int meta) {
		RelativeSide dir = RelativeSide.FDtoRS( ForgeDirection.getOrientation(_side) , direction);
		
		switch(dir) {
		case LADDER:
			return ladder_lights;
		case FRONT:
			return front_lights;
		case LEFT:
		case RIGHT:
			return side_lights;
		case BOTTOM:
			return transparent;//bottom_lights;
		}
		
	    return transparent;
	}

	@Override
	public boolean isSideSolid(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
		if(side == ForgeDirection.UP)
			return false;
		return !isOpen(RelativeSide.FDtoRS(side, direction), w.getBlockMetadata(x,y,z));
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess w, int x, int y, int z, int _side)
	{
		ForgeDirection dir = ForgeDirection.getOrientation(_side);
		if(dir == ForgeDirection.DOWN) {
			if(w.isSideSolid(x, y, z, ForgeDirection.DOWN, false))
				return false;
		}
	    int meta = w.getBlockMetadata(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
	
	    if(isOpen(RelativeSide.FDtoRS(dir, direction), meta)) {
	    	return false;
	    }
	    
	    return true;
	}
	
	
	//==============================================================================
	// Collision methods
	//==============================================================================
	
	@Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB blockBounds, List list, Entity collidingEntity) {
    	if(collidingEntity == null)
			return;
	
		float px = 1/16F;
		
		float out = px;///2;
		float in = out+px;
    	
		int meta = world.getBlockMetadata(x, y, z);
    	
    	if(!isOpen(RelativeSide.FDtoRS(ForgeDirection.NORTH, direction), meta)) {
    		addToList(world, x, y, z, blockBounds, list, out, 0, out, 1-out, 1, in);
    	}
    	
    	if(!isOpen(RelativeSide.FDtoRS(ForgeDirection.SOUTH, direction), meta)) {
    		addToList(world, x, y, z, blockBounds, list, out, 0, 1-out, 1-out, 1, 1-in);
    	}
    	
    	if(!isOpen(RelativeSide.FDtoRS(ForgeDirection.EAST, direction), meta)) {
    		addToList(world, x, y, z, blockBounds, list, 1-in, 0, out, 1-out, 1, 1-out);
    	}
    	
    	if(!isOpen(RelativeSide.FDtoRS(ForgeDirection.WEST, direction), meta)) {
    		addToList(world, x, y, z, blockBounds, list, out, 0, out, in, 1, 1-out);
    	}
    	
    	if(!isOpen(RelativeSide.FDtoRS(ForgeDirection.DOWN, direction), meta)) {
    		addToList(world, x, y, z, blockBounds, list, out, 0, out, 1-out, px, 1-out);
    	}
    }
	
	public void addToList(World world, int x, int y, int z, AxisAlignedBB blockBounds, List list, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		AxisAlignedBB axisalignedbb1 = AxisAlignedBB.getBoundingBox(
        		(double)x + minX, (double)y + minY, (double)z + minZ,
        		(double)x + maxX, (double)y + maxY, (double)z + maxZ
        		);
        		
		if (axisalignedbb1 != null && blockBounds.intersectsWith(axisalignedbb1))
        {
            list.add(axisalignedbb1);
        }
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		
//		System.out.println("COLLIDE!");
	}
	
	//==============================================================================
	// Data manipulation methods
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

	public boolean isOpen(RelativeSide side, int meta) {
		switch(side) {
		case BOTTOM:
			return getBit(meta, 3);
		case FRONT:
			return getBit(meta, 2);
		case LEFT:
			return getBit(meta, 1);
		case RIGHT:
			return getBit(meta, 0);
		default:
			return false;
		}
	}
	
	public boolean isOpen(ForgeDirection side, int meta) {
		return isOpen(RelativeSide.FDtoRS(side, direction), meta);
	}
	
	public void updateOpenData(World world, int x, int y, int z, RelativeSide side, boolean value) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(side) {
		case BOTTOM:
			meta = setBit(meta, 3, value);
			break;
		case FRONT:
			meta = setBit(meta, 2, value);
			break;
		case LEFT:
			meta = setBit(meta, 1, value);
			break;
		case RIGHT:
			meta = setBit(meta, 0, value);
			break;
		}
		world.setBlock(x, y, z, this, meta, 3);
	}

	public void updateIdData(World world, int x, int y, int z, ForgeDirection facing, boolean lights) {
		int meta = world.getBlockMetadata(x,y,z);
		Block b = this;
		switch(facing) {
		case NORTH:
			if(lights)
				b = CatwalkMod.ladderNorthLit;
			else
				b = CatwalkMod.ladderNorthUnlit;
			break;
		case SOUTH:
			if(lights)
				b = CatwalkMod.ladderSouthLit;
			else
				b = CatwalkMod.ladderSouthUnlit;
			break;
		case EAST:
			if(lights)
				b = CatwalkMod.ladderEastLit;
			else
				b = CatwalkMod.ladderEastUnlit;
			break;
		case WEST:
			if(lights)
				b = CatwalkMod.ladderWestLit;
			else
				b = CatwalkMod.ladderWestUnlit;
			break;
		default:
		}
		
		world.setBlock(x, y, z, b, meta, 3);
	}
	
	//==============================================================================
	// Render type methods
	//==============================================================================

	public int getRenderType(){
	    return CatwalkMod.ladderRenderType;
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
	    return super.isBlockNormalCube();
	}

	public boolean isNormalCube()
	{
	    return false;
	}

	public static enum RelativeSide {
		LADDER, FRONT, LEFT, RIGHT, TOP, BOTTOM;
		
		public static RelativeSide FDtoRS(ForgeDirection side, ForgeDirection facing) {
			switch(facing) {
			case NORTH:
				switch(side) {
				case NORTH:
					return RelativeSide.LADDER;
				case SOUTH:
					return RelativeSide.FRONT;
				case EAST:
					return RelativeSide.RIGHT;
				case WEST:
					return RelativeSide.LEFT;
				case UP:
					return RelativeSide.TOP;
				case DOWN:
					return RelativeSide.BOTTOM;
				}
			case SOUTH:
				switch(side) {
				case NORTH:
					return RelativeSide.FRONT;
				case SOUTH:
					return RelativeSide.LADDER;
				case EAST:
					return RelativeSide.LEFT;
				case WEST:
					return RelativeSide.RIGHT;
				case UP:
					return RelativeSide.TOP;
				case DOWN:
					return RelativeSide.BOTTOM;
				}
			case EAST:
				switch(side) {
				case NORTH:
					return RelativeSide.RIGHT;
				case SOUTH:
					return RelativeSide.LEFT;
				case EAST:
					return RelativeSide.FRONT;
				case WEST:
					return RelativeSide.LADDER;
				case UP:
					return RelativeSide.TOP;
				case DOWN:
					return RelativeSide.BOTTOM;
				}
			case WEST:
				switch(side) {
				case NORTH:
					return RelativeSide.LEFT;
				case SOUTH:
					return RelativeSide.RIGHT;
				case EAST:
					return RelativeSide.LADDER;
				case WEST:
					return RelativeSide.FRONT;
				case UP:
					return RelativeSide.TOP;
				case DOWN:
					return RelativeSide.BOTTOM;
				}
			default:
				switch(side) {
				case NORTH:
					return RelativeSide.LADDER;
				case SOUTH:
					return RelativeSide.FRONT;
				case EAST:
					return RelativeSide.RIGHT;
				case WEST:
					return RelativeSide.LEFT;
				case UP:
					return RelativeSide.TOP;
				case DOWN:
					return RelativeSide.BOTTOM;
				}
			}
			return RelativeSide.BOTTOM;
		}
	
		public static ForgeDirection RStoFD(RelativeSide side, ForgeDirection facing) {
			switch(facing) {
			case NORTH:
				switch(side) {
				case LADDER:
					return ForgeDirection.NORTH;
				case FRONT:
					return ForgeDirection.SOUTH;
				case RIGHT:
					return ForgeDirection.EAST;
				case LEFT:
					return ForgeDirection.WEST;
				case TOP:
					return ForgeDirection.UP;
				case BOTTOM:
					return ForgeDirection.DOWN;
				}
			case SOUTH:
				switch(side) {
				case FRONT:
					return ForgeDirection.NORTH;
				case LADDER:
					return ForgeDirection.SOUTH;
				case LEFT:
					return ForgeDirection.EAST;
				case RIGHT:
					return ForgeDirection.WEST;
				case TOP:
					return ForgeDirection.UP;
				case BOTTOM:
					return ForgeDirection.DOWN;
				}
			case EAST:
				switch(side) {
				case RIGHT:
					return ForgeDirection.NORTH;
				case LEFT:
					return ForgeDirection.SOUTH;
				case FRONT:
					return ForgeDirection.EAST;
				case LADDER:
					return ForgeDirection.WEST;
				case TOP:
					return ForgeDirection.UP;
				case BOTTOM:
					return ForgeDirection.DOWN;
				}
			case WEST:
				switch(side) {
				case LEFT:
					return ForgeDirection.NORTH;
				case RIGHT:
					return ForgeDirection.SOUTH;
				case LADDER:
					return ForgeDirection.EAST;
				case FRONT:
					return ForgeDirection.WEST;
				case TOP:
					return ForgeDirection.UP;
				case BOTTOM:
					return ForgeDirection.DOWN;
				}
			default:
				switch(side) {
				case LADDER:
					return ForgeDirection.NORTH;
				case FRONT:
					return ForgeDirection.SOUTH;
				case RIGHT:
					return ForgeDirection.EAST;
				case LEFT:
					return ForgeDirection.WEST;
				case TOP:
					return ForgeDirection.UP;
				case BOTTOM:
					return ForgeDirection.DOWN;
				}
			}
			return ForgeDirection.DOWN;
		}
	}
	
	//==============================================================================
	// ICagedLadderConnectable
	//==============================================================================
	
	@Override
	public boolean shouldConnectToSide(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		return isOpen(RelativeSide.FDtoRS(side, direction), w.getBlockMetadata(x,y,z));
	}

	@Override
	public boolean shouldHaveBottom(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		if( !isOpen(RelativeSide.BOTTOM, w.getBlockMetadata(x,y,z)) )
			return true;
		Block b = w.getBlock(x,y-1,z);
		if( b instanceof ICagedLadderConnectable) {
			System.out.println(side);
			if( ((ICagedLadderConnectable)b).doesSideHaveWall(w, x, y-1, z, side) ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean doesSideHaveWall(IBlockAccess w, int x, int y, int z,
			ForgeDirection side) {
		return !isOpen(RelativeSide.FDtoRS(side, direction), w.getBlockMetadata(x,y,z));
	}
	
	@Override
	public boolean isThin(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
		return true;
	}
}
