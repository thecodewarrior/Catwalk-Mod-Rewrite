package com.thecodewarrior.catwalks.legacy;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLegacyCagedLadder extends BlockContainer {

	public BlockLegacyCagedLadder() {
		super(Material.iron);
		setBlockName("legacy.ladder");
		setBlockTextureName("catwalkmod:ladder");
		setHardness(1.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityCagedLadder();
	}
	
}
