package com.thecodewarrior.catwalks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.ICustomLadder;

public class CatwalkOptions {
	
	public boolean fullBlockLadder = false;
	public float ladderSpeedMultiplier = 1;
	public float speedPotionLevel = 1;
	public boolean vanillaLadders = false;
    public boolean altGrateRecipe = false;
	
	public void init() {}
	
	public void load(Configuration config) {
		
		config.load();
		
		fullBlockLadder 		= config.getBoolean("FullBlockLadder", "catwalks", fullBlockLadder,				 "Set to true to be able to climb the outside of caged ladders");
		ladderSpeedMultiplier 	= config.getFloat(    "ladderSpeed",     "catwalks", ladderSpeedMultiplier, 0, 10, "Ladders will go at 5*N blocks/second");
		speedPotionLevel 		= config.getFloat(    "catwalkSpeed",    "catwalks", speedPotionLevel,      0, 10, "Catwalk speed boost will be equivalent to a Speed N potion.");
		vanillaLadders			= config.getBoolean("VanillaLadders",  "catwalks", vanillaLadders,				 "Set to true to turn leaves, bookcases, and iron bars into ladders");
		altGrateRecipe			= config.getBoolean("AlternateSteelGrateRecipe",  "catwalks", altGrateRecipe,	 "Use alternate steel grate recipe with iron bars (in case of mod conflicts)");

		config.save();
		
	}
	
	public void postInit() {
		if(vanillaLadders)
			initCustomLadders();
	}
	
	public void initCustomLadders() {
		CustomLadderRegistry.registerCustomLadder(Blocks.iron_bars, new ICustomLadder() {

			@Override
			public double getLadderVelocity(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return 0.1;
			}

			@Override
			public double getLadderFallVelocity(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity) {
				return -1;
			}

			@Override
			public boolean isOnLadder(IBlockAccess world, int x, int y, int z,
					EntityLivingBase entity) {
				Block b = world.getBlock(x,y,z);
				if(!(b instanceof BlockPane))
					return false;
				BlockPane bp = (BlockPane)b;
				double d = 6/16F;
				double dNorth = bp.canPaneConnectTo(world, x, y, z-1, ForgeDirection.NORTH) ? 0 : d;
				double dSouth = bp.canPaneConnectTo(world, x, y, z+1, ForgeDirection.SOUTH) ? 0 : d;
				double dEast  = bp.canPaneConnectTo(world, x+1, y, z, ForgeDirection.EAST ) ? 0 : d;
				double dWest  = bp.canPaneConnectTo(world, x-1, y, z, ForgeDirection.WEST ) ? 0 : d;
								
				AxisAlignedBB northSouthBox = AxisAlignedBB.getBoundingBox(x+d,     y, z+dNorth, x+1-d,     y+1, z+1-dSouth);
				AxisAlignedBB eastWestBox   = AxisAlignedBB.getBoundingBox(x+dWest, y, z+d,      x+1-dEast, y+1, z+1-d     );
				
		        return entity.boundingBox.intersectsWith(northSouthBox) || entity.boundingBox.intersectsWith(eastWestBox);
			}

			@Override
			public boolean shouldPlayStepSound(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity, boolean isMovingDown) {
				return !isMovingDown;
			}

			@Override
			public boolean shouldHoldOn(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return entity.isSneaking();
			}

			@Override
			public boolean shouldClimbDown(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return false;
			}

			@Override
			public double getClimbDownVelocity(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity) {
				return 0;
			}
			
		});
		
		ICustomLadder leafLadder = new ICustomLadder() {

			@Override
			public double getLadderVelocity(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return 0.05;
			}

			@Override
			public double getLadderFallVelocity(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity) {
				return -1;
			}

			@Override
			public boolean isOnLadder(IBlockAccess world, int x, int y, int z,
					EntityLivingBase entity) {
				return true;
			}

			@Override
			public boolean shouldPlayStepSound(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity, boolean isMovingDown) {
				return !isMovingDown;
			}

			@Override
			public boolean shouldHoldOn(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return entity.isSneaking();
			}

			@Override
			public boolean shouldClimbDown(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return false;
			}

			@Override
			public double getClimbDownVelocity(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity) {
				return 0;
			}
			
		};
		
		CustomLadderRegistry.registerCustomLadder(Blocks.leaves, leafLadder);
		CustomLadderRegistry.registerCustomLadder(Blocks.leaves2, leafLadder);
		
		CustomLadderRegistry.registerCustomLadder(Blocks.bookshelf, new ICustomLadder() {

			@Override
			public double getLadderVelocity(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return 0.05;
			}

			@Override
			public double getLadderFallVelocity(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity) {
				return -1;
			}

			@Override
			public boolean isOnLadder(IBlockAccess world, int x, int y, int z,
					EntityLivingBase entity) {
				return true;
			}

			@Override
			public boolean shouldPlayStepSound(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity, boolean isMovingDown) {
				return !isMovingDown;
			}

			@Override
			public boolean shouldHoldOn(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return entity.isSneaking();
			}

			@Override
			public boolean shouldClimbDown(IBlockAccess world, int x, int y,
					int z, EntityLivingBase entity) {
				return false;
			}

			@Override
			public double getClimbDownVelocity(IBlockAccess world, int x,
					int y, int z, EntityLivingBase entity) {
				return 0;
			}
			
		});
	}

}
