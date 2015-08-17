package com.thecodewarrior.catwalks;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import buildcraft.api.tools.IToolWrench;

import com.thecodewarrior.codechicken.lib.vec.BlockCoord;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class CommonProxy {

	public boolean isClient = false;
	
	public LinkedList<WeakReference<EntityLivingBase>> entities = new LinkedList<WeakReference<EntityLivingBase>>();

	public int lastOp = -1;
	
	public void init() {
		// do some server stuff
		initClient();
	}
	
	public void initClient() {}
	
	public void postInit() {}
	
	public void performModification() {
		int opNum = 5;
		if(lastOp == -1) {
			lastOp = opNum;
		}
		if(lastOp < opNum) {
			System.out.println("Performing operation " + opNum);
			// BEGIN CODE
			if(CatwalkMod.proxy instanceof ClientProxy) {
				((ClientProxy)CatwalkMod.proxy).maxSpeed = 0;
			}
			// END CODE
			lastOp = opNum;
		}
	}
	
	public static boolean isHoldingUsableWrench(EntityPlayer player)
    {
      Item equipped = player.inventory.getCurrentItem() != null ? player.inventory.getCurrentItem().getItem() : null;
      return equipped instanceof IToolWrench;
    }
	
	public CatwalkEntityProperties getOrCreateEP(Entity entity) {
		CatwalkEntityProperties catwalkEP = (CatwalkEntityProperties)entity.getExtendedProperties("catwalkmod.catwalkdata");
		if(catwalkEP == null) {
			catwalkEP = new CatwalkEntityProperties();
			entity.registerExtendedProperties("catwalkmod.catwalkdata", catwalkEP);
		}
		return catwalkEP;
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		// copying minecraft's ladder code, just with customizable velocity
		
		
		BlockCoord coord = getLadderCoord(event.entityLiving); // find any caged ladders
		EntityLivingBase e = event.entityLiving;

		CatwalkEntityProperties catwalkEP = getOrCreateEP(event.entity); // get entity properties object "for future uses"
		if(coord.y >= 0) { // if the block was found (y=-1 if not found)

			Block b = event.entity.worldObj.getBlock(coord.x, coord.y, coord.z); // get the custom ladder block
			ICustomLadderVelocity icl = (ICustomLadderVelocity)b;
			double   upSpeed = icl.getLadderVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
			double downSpeed = icl.getLadderFallVelocity(e.worldObj, coord.x, coord.y, coord.z, e); // get custom fall velocity

			if(e.isCollidedHorizontally) { // entity is smashed up against something
				e.motionY = upSpeed; // set the entity's upward velocity to the custom value
				catwalkEP.highSpeedLadder = true; // now when they stop they'll be slowed down to 0.2 whatevers when they stop
			} else {
				e.fallDistance = 0.0F; // reset fall distance to prevent fall damage
				
                if (e.motionY < -downSpeed) // if the entity is falling faster than custom fall velocity
                {
                    e.motionY = -downSpeed; // set entity's velocity to the custom fall velocity
                }

                boolean shouldStopOnLadder = e.isSneaking() && e instanceof EntityPlayer;

                if (shouldStopOnLadder && e.motionY < 0.0D) { // should stop and entity is moving down
    				e.motionY = 0.0D; // don't you DARE move down
                }
			}
			
			double dX = e.posX - catwalkEP.lastStepX;
			double dY = e.posY - catwalkEP.lastStepY;
			double dZ = e.posZ - catwalkEP.lastStepZ;
			
			double distanceClimbed = Math.abs(dY);//MathHelper.sqrt_double( (dX * dX ) + (dY * dY) + (dZ * dZ));
			double distanceRequired = upSpeed * 10;
			
			if(catwalkEP.isSlidingDownLadder && dY >= 0) {
				distanceRequired = 0;
			}
			catwalkEP.isSlidingDownLadder = (dY < 0);
			
			if(distanceClimbed > distanceRequired) {
				catwalkEP.lastStepX = e.posX;
				catwalkEP.lastStepY = e.posY;
				catwalkEP.lastStepZ = e.posZ;
				boolean shouldPlay = dY < 0 ?
						icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, true) :
						icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, false);
						
						
		        if(shouldPlay) {
		        	Block.SoundType soundtype = e.worldObj.getBlock(coord.x, coord.y, coord.z).stepSound;
					e.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
		        }
			}
			
		}
		
		
		if(catwalkEP.highSpeedLadder && !event.entityLiving.isCollidedHorizontally) {
			if(event.entity.motionY > 0.2D)
				event.entity.motionY = 0.2D; // slow down entity once they stop climbing to prevent them flying upwards

			catwalkEP.highSpeedLadder = false;
		}
	}
	
	public BlockCoord getLadderCoord(EntityLivingBase entity) {
		
		return findCollidingBlock(entity, new Matcher<BlockCoord, EntityLivingBase>(entity) {

			@Override
			public boolean match(BlockCoord bc) {
				Block b = arg.worldObj.getBlock(bc.x, bc.y, bc.z);
				return  b != null &&
						b instanceof ICustomLadderVelocity &&
						( (ICustomLadderVelocity)b).isOnLadder(arg.worldObj, bc.x, bc.y, bc.z, arg);
			}
		});
	}
	
	/**
	 * Shamelessly stolen from {net.minecraftforge.common.ForgeHooks.isLivingOnLadder}
	 * @param entity
	 * @param mat
	 * @return
	 */
	public BlockCoord findCollidingBlock(EntityLivingBase entity, Matcher<BlockCoord, ?> mat) {
		World world = entity.worldObj;
        Block block;
		AxisAlignedBB bb = entity.boundingBox;
        int mX = MathHelper.floor_double(bb.minX);
        int mY = MathHelper.floor_double(bb.minY);
        int mZ = MathHelper.floor_double(bb.minZ);
        for (int y2 = mY; y2 < bb.maxY; y2++)
        {
            for (int x2 = mX; x2 < bb.maxX; x2++)
            {
                for (int z2 = mZ; z2 < bb.maxZ; z2++)
                {
                	
                	BlockCoord bc = new BlockCoord(x2, y2, z2);
                    if (mat.match(bc))
                    {
                        return bc;
                    }
                }
            }
        }
        return new BlockCoord(0,-1,0);
	}
	
	public static abstract class Matcher<T, A> {
		public A arg;
		
		public Matcher(A arg) {
			this.arg = arg;
		}
		
		public abstract boolean match(T obj);
	}

	@SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
    	performModification(); // for quickly running code in a dev environment
		
    	if( event.phase == Phase.END) {
    		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    		
    		for (final EntityPlayerMP player : players) { // for each player
    			// find any catwalks
				BlockCoord coord = findCollidingBlock(player, new Matcher<BlockCoord, EntityPlayerMP>(player) {
					@Override
					public boolean match(BlockCoord bc) { 
						Block b = arg.worldObj.getBlock(bc.x, bc.y, bc.z);
						return  b != null &&
								b instanceof BlockCatwalk;
					}
				});
				
				
				
				IAttributeInstance attrInstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
				AttributeModifier m = attrInstance.getModifier(CatwalkMod.speedModifier.getID());
				
				if(coord.y == -1) { // if no catwalks found
					if(m != null) { // and speed modifier is still applied
						attrInstance.removeModifier(CatwalkMod.speedModifier); // remove it
					}
					continue;
				}
				
				double multiplier = 0.2D; // roughly the same as a Swiftness I potion
				
				if(m == null || m.getAmount() != multiplier ) { // if modifier isn't applied or the amount has changed
					attrInstance.removeModifier(CatwalkMod.speedModifier); // remove the modifier
					attrInstance.applyModifier(
			        		new AttributeModifier(CatwalkMod.speedModifier.getID(), "catwalkmod.speedup",
			        				multiplier, 2)); // re-apply it
				}
			} // end for
    		
    	}
	}
}
