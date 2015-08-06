package com.thecodewarrior.catwalks;

import java.util.List;

import codechicken.lib.vec.Cuboid6;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCagedLadder extends Block implements ICustomLadderVelocity {

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
	
	public IIcon transparent;
	
	// meta: bottom, front, left, right
	
	public BlockCagedLadder(ForgeDirection direction, boolean lights) {
		super(Material.iron);
		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		setBlockName("cagedladder");
		if(direction == ForgeDirection.NORTH && !lights)
			setCreativeTab(CreativeTabs.tabTransport);
		setHarvestLevel("wrench", 0);
		setHarvestLevel("pickaxe", 0);
		this.lights = lights;
		this.direction = direction;
	}
	
	@Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
		float px = 1/16;
		float px2 = 2*px;
		float d = 3/32F;
//        return entity.boundingBox.intersectsWith(AxisAlignedBB.getBoundingBox(x+d, y, z+d, x+1-d, y+1, z+1-d));
		return true;
    }
	
	public double getLadderVelocity(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return entity.isSneaking() ? 0.2D : 0.25D;
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
				return transparent;//bottom_with_lights;
			else
				return transparent;//bottom;
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
	// Collision methods
	//==============================================================================
	
	@Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB blockBounds, List list, Entity collidingEntity) {
    	if(collidingEntity == null)
			return;
	
		float px = 1/16F;
		float px2 = 2*px;

		float outsideDistance = 1/32F;
		float insideDistance  = 3/32F;
		
		float out = px/2;
		float in = out+px;
    	
		int meta = world.getBlockMetadata(x, y, z);
    	
//    	Cuboid6 oldBounds = new Cuboid6(
//    			this.getBlockBoundsMinX(), this.getBlockBoundsMinY(), this.getBlockBoundsMinZ(),
//    			this.getBlockBoundsMaxX(), this.getBlockBoundsMaxY(), this.getBlockBoundsMaxZ()
//    		);
    	
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
    	
    	setBlockBounds(px, 0, px, 1-px, 1, 1-px);
    	
//    	oldBounds.setBlockBounds(this);
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
	
	public boolean isOpen(RelativeSide side, int meta) {
		switch(side) {
		case BOTTOM:
			if(( meta & 8 ) > 0) {
				return true;
			}
			break;
		case FRONT:
			if(( meta & 4 ) > 0) {
				return true;
			}
			break;
		case LEFT:
			if(( meta & 2 ) > 0) {
				return true;
			}
			break;
		case RIGHT:
			if(( meta & 1 ) > 0) {
				return true;
			}
			break;
		default:
			return false;
		}
		return false;
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
	    return false;
	}

	public boolean isNormalCube()
	{
	    return false;
	}
}
