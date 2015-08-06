package com.thecodewarrior.catwalks;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import buildcraft.api.tools.IToolWrench;
import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class CommonProxy {

	public LinkedList<WeakReference<EntityLivingBase>> entities = new LinkedList<WeakReference<EntityLivingBase>>();

	public void init() {
		// do some server stuff
		initClient();
	}
	
	public void initClient() {}
	
	public void postInit() {}
	
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

	public void speedupPlayer(World world, Entity entity, double multiplier) {
		if(!( entity instanceof EntityLivingBase ))
    		return;
    	EntityLivingBase e = (EntityLivingBase) entity;
		CatwalkEntityProperties catwalkEP = getOrCreateEP(e); // (CatwalkEntityProperties)e.getExtendedProperties("catwalkmod.catwalkdata");
		
//		if(catwalkEP == null) {
//			catwalkEP = new CatwalkEntityProperties();
//			entity.registerExtendedProperties("catwalkmod.catwalkdata", catwalkEP);
//		}
		
		catwalkEP.multiplier = multiplier;
		if(!catwalkEP.isInList) {
	        entities.add(new WeakReference<EntityLivingBase>(e));
	        
			catwalkEP.isInList = true;
		}
		IAttributeInstance attrInstance = e.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		attrInstance.removeModifier(CatwalkMod.speedModifier);
		attrInstance.applyModifier(
        		new AttributeModifier(CatwalkMod.speedModifier.getID(), "catwalkmod.speedup",
        				catwalkEP.multiplier, 2));
		catwalkEP.timeout = 10;
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		BlockCoord coord = getLadderCoord(event.entityLiving);
		CatwalkEntityProperties catwalkEP = getOrCreateEP(event.entity);
		
		if(coord.y >= 0) {
			Block b = event.entity.worldObj.getBlock(coord.x, coord.y, coord.z);
			EntityLivingBase e = event.entityLiving;
			
			if(e.isCollidedHorizontally) {
				e.motionY = ((ICustomLadderVelocity)b).getLadderVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
				catwalkEP.highSpeedLadder = true;
			} else {
				e.fallDistance = 0.0F;

                if (e.motionY < -0.15D)
                {
                    e.motionY = -0.15D;
                }

                boolean shouldStopOnLadder = e.isSneaking() && e instanceof EntityPlayer;

                if (shouldStopOnLadder && e.motionY < 0.0D) {
                    e.motionY = 0.0D;
                }
			}
		}
		
		if(catwalkEP.highSpeedLadder && !event.entityLiving.isCollidedHorizontally) {
			event.entity.motionY = 0.2D;
			catwalkEP.highSpeedLadder = false;
		}
	}
	
	public BlockCoord getLadderCoord(EntityLivingBase entity) {
		
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
                	
                    block = world.getBlock(x2, y2, z2);
                    if (block != null && block.isLadder(world, x2, y2, z2, entity) && block instanceof ICustomLadderVelocity)
                    {
                        return new BlockCoord(x2,y2,z2);
                    }
                }
            }
        }
        return new BlockCoord(0,-1,0);
	}

	@SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
    	if( event.phase == Phase.END) {
    		Iterator<WeakReference<EntityLivingBase>> iter = entities.iterator();
        	while(iter.hasNext()) {
        		WeakReference<EntityLivingBase> e = iter.next();
        		if(e.get() == null) {
        			iter.remove();
        			continue;
        		}
        		CatwalkEntityProperties catwalkEP = (CatwalkEntityProperties)e.get().getExtendedProperties("catwalkmod.catwalkdata");
        		catwalkEP.timeout--;
        		
        		if(catwalkEP.timeout < 0) {
        			IAttributeInstance attrInstance = e.get().getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            		attrInstance.removeModifier(CatwalkMod.speedModifier);
        			if(e.get() instanceof EntityPlayer)
        				System.out.println(String.format("Removing player %s from list", ((EntityPlayer) e.get()).getDisplayName()));
            		catwalkEP.isInList = false;
        			iter.remove();
        			continue;
        		}
        		/* if(catwalkEP.refreshTimeout) {
        			IAttributeInstance attrInstance = e.get().getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            		attrInstance.removeModifier(CatwalkMod.speedModifier);
        			attrInstance.applyModifier(
        	        		new AttributeModifier(CatwalkMod.speedModifier.getID(), "catwalkmod.speedup",
        	        				catwalkEP.multiplier, CatwalkMod.speedModifier.getOperation()));
        		} */
			}
    	}
	}
}
