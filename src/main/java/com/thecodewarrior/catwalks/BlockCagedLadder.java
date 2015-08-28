package com.thecodewarrior.catwalks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.codechicken.lib.raytracer.ExtendedMOP;
import com.thecodewarrior.codechicken.lib.raytracer.IndexedCuboid6;
import com.thecodewarrior.codechicken.lib.raytracer.RayTracer;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;
import com.thecodewarrior.codechicken.lib.vec.Cuboid6;
import com.thecodewarrior.codechicken.lib.vec.Vector3;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCagedLadder extends Block implements ICustomLadder, ICagedLadderConnectable {

	RayTracer rayTracer = new RayTracer();
	
	public ForgeDirection direction;
	public boolean lights;
	public boolean isBottomOpen;
	public boolean tape;
	
	public Map<TextureSide, Map<TextureType, IIcon>> textures;
	
	public enum TextureSide {
		LADDER("ladder"),
		SIDE("side"),
		FRONT("front"),
		BOTTOM("bottom");
		
		public String filename;
		private TextureSide(String filename) {
			this.filename = filename;
		}
		
		public static TextureSide fromRS(RelativeSide side) {
			switch(side) {
			case FRONT:
				return FRONT;
			case LEFT:
			case RIGHT:
				return SIDE;
			case LADDER:
				return LADDER;
			case BOTTOM:
				return BOTTOM;
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
	
	public IIcon landing;
	public IIcon landing_tape;
	
	public IIcon transparent;
	
	public Map<RelativeSide, Cuboid6> closed = new HashMap<RelativeSide, Cuboid6>();
	public Map<RelativeSide, Cuboid6>   open = new HashMap<RelativeSide, Cuboid6>();
	
	// meta: bottom, front, left, right
	
	public BlockCagedLadder(ForgeDirection direction, boolean lights, boolean bottom, boolean tape) {
		super(Material.iron);
		setHardness(1.0F);
		setStepSound(Block.soundTypeMetal);
		float px = 1/16F;
    	setBlockBounds(px, 0, px, 1-px, 1, 1-px);
		setBlockName("cagedladder");
		setStepSound(soundTypeLadder);
		if(direction == ForgeDirection.NORTH && !lights && !bottom)
			setCreativeTab(CreativeTabs.tabTransport);
		setHarvestLevel("wrench", 0);
		setHarvestLevel("pickaxe", 0);
		this.lights = lights;
		this.direction = direction;
		this.isBottomOpen = bottom;
		this.tape = tape;
		initHitBoxes();
	}
	
	//==============================================================================
	// Place/Destroy methods
	//==============================================================================
	
    @Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase e, ItemStack s) {
    	int l = MathHelper.floor_double((double)((e.rotationYaw * 4F) / 360F) + 0.5D) & 3;
    	ForgeDirection d = ForgeDirection.NORTH;
    	switch(l) {
    	case 0:
    		d = ForgeDirection.SOUTH; break;
    	case 1:
    		d = ForgeDirection.EAST;  break;
    	case 2:
    		d = ForgeDirection.NORTH; break;
    	case 3:
    		d = ForgeDirection.WEST;  break;
    	}
    	updateIdData(w, x, y, z, d, lights, isBottomOpen, tape);
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
			( (BlockCagedLadder)b ).updateBottom(world, x, y+1, z);
		}
		if(self)
			this.updateBottom(world, x, y, z);
	}
	
	public void updateBottom(World world, int x, int y, int z) {
		ForgeDirection direction = ((BlockCagedLadder)world.getBlock(x,y,z)).direction;
		if(world.getBlock(x, y-1, z) instanceof BlockCagedLadder) {
			updateIdData(world, x, y, z, direction, lights, true, tape);
		} else {
			updateIdData(world, x, y, z, direction, lights, false, tape);
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
			if(CatwalkUtil.isHoldingWrench(player))
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
		
		if(CatwalkUtil.isHoldingWrench(player)) {
			if(player.isSneaking()) {

//				this.dropBlockAsItem(world, x, y, z, 0, 0);
				List<ItemStack> drops = this.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
				for(ItemStack s : drops) {
					CatwalkUtil.giveItemToPlayer(player, s);
				}
				this.updateNeighborSides(world, x, y, z, false);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int blockSide, float hitX, float hitY, float hitZ) {
		MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
		
		RelativeSide side = RelativeSide.TOP;
		if (hit != null) {
			side = (RelativeSide) ( (ExtendedMOP) hit ).data;
		}
				
		ItemStack handStack = player.getCurrentEquippedItem();

		if(CatwalkUtil.isHoldingWrench(player) && !player.isSneaking()) {
			updateOpenData(world, x, y, z, side, !isOpen(side, world.getBlockMetadata(x, y, z)));
		}
		
		
		if(handStack != null) {
			Item item = handStack.getItem();
			boolean use = false;
			
			if(item instanceof ItemRopeLight && this.lights == false) {
				updateIdData(world, x, y, z, direction, true, isBottomOpen, tape);
				use = true;
			}
			
			if(item instanceof ItemCautionTape && this.tape == false) {
				updateIdData(world, x, y, z, direction, lights, isBottomOpen, true);
				use = true;
			}
			
			if(use && !player.capabilities.isCreativeMode)
				handStack.stackSize--;
		}
		
		if(player.isSneaking()) {
			if(this.lights && (handStack == null || handStack.getItem() == CatwalkMod.itemRopeLight)) {
				if(!world.isRemote) {
//					world.spawnEntityInWorld(new EntityItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(CatwalkMod.itemRopeLight, 1)));
					CatwalkUtil.giveItemToPlayer(player, new ItemStack(CatwalkMod.itemRopeLight, 1));
					updateIdData(world, x, y, z, direction, false, isBottomOpen, tape);
				}
			} else if(this.tape && (handStack == null || handStack.getItem() == CatwalkMod.itemCautionTape)) {
				if(!world.isRemote) {
//					world.spawnEntityInWorld(new EntityItem(world, x+0.5, y+0.5, z+0.5, new ItemStack(CatwalkMod.itemCautionTape, 1)));
					CatwalkUtil.giveItemToPlayer(player, new ItemStack(CatwalkMod.itemCautionTape, 1));
					updateIdData(world, x, y, z, direction, lights, isBottomOpen, false);
				}
			}
		}
		
		return false;
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
		open.put(RelativeSide.LADDER, getOpenHighlightCuboid(open, RelativeSide.LADDER, direction) );
		open.put(RelativeSide.BOTTOM, getOpenHighlightCuboid(open, RelativeSide.BOTTOM, direction) );
		open.put(RelativeSide.TOP, getOpenHighlightCuboid(open, RelativeSide.TOP, direction) );
	}

	public Cuboid6 getOpenHighlightCuboid(Map<RelativeSide, Cuboid6> map, RelativeSide side, ForgeDirection facing) {
//		double width = 0.25D;
		double sub = 11/16D;//1-(1/16D)-width;
		double frontSub = sub/2;
		
		Cuboid6 cuboid = closed.get(side).copy();
//		if(side == RelativeSide.LADDER)
//			return cuboid;
			
		if(side == RelativeSide.TOP) {
			cuboid.min.x += frontSub;
			cuboid.min.z += frontSub;

			cuboid.max.x -= frontSub;
			cuboid.max.z -= frontSub;
		} else if(side != RelativeSide.FRONT && side != RelativeSide.LADDER) {
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
		return collisionRayTrace(world, x, y, z, CatwalkMod.proxy.getPlayerLooking(start, end), start, end);
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, EntityPlayer player, Vec3 start, Vec3 end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        
        boolean hasWrench = true;
        if(player != null)
        	hasWrench = CatwalkUtil.isHoldingWrench(player);
    	
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
			if(rs == RelativeSide.TOP) {
				continue;
			}
			if(isOpen(rs, meta)) {
				if(hasWrench)
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
	    
	    textures = new HashMap<BlockCagedLadder.TextureSide, Map<TextureType,IIcon>>();
	    for (TextureSide side : TextureSide.values()) {
	    	Map<TextureType, IIcon> sideMap = new HashMap<BlockCagedLadder.TextureType, IIcon>();
	    	textures.put(side, sideMap);
	    	for (TextureType type : TextureType.values()) {
				IIcon icon = reg.registerIcon("catwalks:ladder/" + side.filename + "/" + type.filename);
				
				sideMap.put(type, icon);
			}
		}
	    
		landing 	 = reg.registerIcon("catwalks:ladder/landing");
		landing_tape = reg.registerIcon("catwalks:ladder/landing_tape");
	}
	
	@Override
	public IIcon getIcon(int _side, int meta) {
		ForgeDirection side = ForgeDirection.getOrientation(_side);
		RelativeSide dir = RelativeSide.FDtoRS( side, direction );
		
		if(dir == RelativeSide.TOP) {
		    return transparent;
		}
		
		TextureSide tSide = TextureSide.fromRS(dir);
		TextureType type = TextureType.fromLightsAndTape(lights, tape);
		
		IIcon ic = textures.get(tSide).get(type);
		if(dir == RelativeSide.RIGHT) {
			return new IconFlipped(ic, true, false);
		}

		return ic;
	}
	
	public IIcon getLightIcon(int _side, int meta) {
		RelativeSide dir = RelativeSide.FDtoRS( ForgeDirection.getOrientation(_side) , direction);
		
		if(dir == RelativeSide.TOP) {
		    return transparent;
		}
		
		TextureSide tSide = TextureSide.fromRS(dir);
		
		IIcon ic = textures.get(tSide).get(tape ? TextureType.T_LIGHTS : TextureType.LIGHTS);
		if(dir == RelativeSide.RIGHT) {
			return new IconFlipped(ic, true, false);
		}

		return ic;
	}
	
	public int getLightValue()
	{
	    return this.lights ? CatwalkMod.lightLevel : 0;
	}

	@Override
	public boolean isSideSolid(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
		return false;
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
		
		float out = px;
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
    	
    	if(!isOpen(RelativeSide.FDtoRS(ForgeDirection.DOWN, direction), meta) && !world.isSideSolid(x, y-1, z, ForgeDirection.UP, false)) {
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
		
	}
	
	//==============================================================================
	// Drop methods
	//==============================================================================
	
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
	    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
	
	    ret.add(new ItemStack(
	    		Item.getItemFromBlock(CatwalkMod.defaultLadder),
	    		1));
	    if(this.lights) {
	    	ret.add(new ItemStack(
	    			CatwalkMod.itemRopeLight,
	    			1
	    		));
	    }
	    if(this.tape) {
	    	ret.add(new ItemStack(
	    			CatwalkMod.itemCautionTape,
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
			return isBottomOpen;
		case LADDER:
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
			updateIdData(world, x, y, z, direction, lights, value, tape);
			return;
		case LADDER:
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


	public void updateIdData(World world, int x, int y, int z, ForgeDirection facing, boolean lights, boolean bottom, boolean tape) {
		int meta = world.getBlockMetadata(x,y,z);
//		BlockCagedLadder b = this;
		if(facing == ForgeDirection.UP || facing == ForgeDirection.DOWN || facing == ForgeDirection.UNKNOWN) {
			facing = ForgeDirection.NORTH;
		}
//		if(facing != ForgeDirection.UP && facing != ForgeDirection.DOWN) {
//			Map<ForgeDirection, Map<Boolean, Map<Boolean, Map<Boolean, Block>>>> map = CatwalkMod.ladders;
//			b = (BlockCagedLadder)map.get(facing).get(lights).get(bottom).get(tape);
//		}
		
		world.setBlock(x, y, z, CatwalkMod.ladders.get(facing).get(lights).get(bottom).get(tape), world.getBlockMetadata(x, y, z), 3);

//		world.setBlock(x, y, z, b, meta, 3);
		
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

	@Override
	public boolean shouldPlayStepSound(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity, boolean isMovingDown) {
		return true;
	}

	@Override
	public boolean shouldStopFall(IBlockAccess world, int x, int y, int z,
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
}
