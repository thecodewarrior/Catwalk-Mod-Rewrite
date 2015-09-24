package com.thecodewarrior.catwalks.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.block.BlockCagedLadder;
import com.thecodewarrior.catwalks.block.BlockCatwalk;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.catwalks.util.Predicate;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSupportColumn extends ItemBlock {

	public ItemBlockSupportColumn(Block b) {
		super(b);
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        if(( block instanceof BlockCatwalk || block instanceof BlockCagedLadder ) && world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ) instanceof BlockSupportColumn) {
        	x += side.offsetX;
        	y += side.offsetY;
        	z += side.offsetZ;
        	hitX -= side.offsetX;
        	hitY -= side.offsetY;
        	hitZ -= side.offsetZ;
        	
        	_side = side.getOpposite().ordinal();
        	block = world.getBlock(x,y,z);
        }
        
        return CatwalkUtil.extendBlock(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, this,
	        new Predicate<Block>() { public boolean test(Block block) { /*---*/ return block instanceof BlockSupportColumn; /*---*/ } },
	        new Predicate<BlockCoord>(world) {
				@Override
				public boolean test(BlockCoord coord) {
					World world = (World)args[0];
					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockSupportColumn;
				}
	    	},
	    	new Predicate<BlockCoord>() { public boolean test(BlockCoord coord) { /*---*/ return false; /*---*/ } });
    }
    
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int _side, EntityPlayer player, ItemStack stack)
    {
    	Block block = world.getBlock(x, y, z);
        ForgeDirection side = ForgeDirection.getOrientation(_side);
        
        if(( block instanceof BlockCatwalk || block instanceof BlockCagedLadder ) && world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ) instanceof BlockSupportColumn) {
        	x += side.offsetX;
        	y += side.offsetY;
        	z += side.offsetZ;
        	
        	_side = side.getOpposite().ordinal();
        	block = world.getBlock(x,y,z);
        }
        
        return CatwalkUtil.canPlaceBlock(stack, player, world, x, y, z, _side, this,
                new Predicate<Block>() { public boolean test(Block block) { /*---*/ return block instanceof BlockSupportColumn; /*---*/ } },
                new Predicate<BlockCoord>(world) {
        			@Override
        			public boolean test(BlockCoord coord) {
        				World world = (World)args[0];
        				return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockSupportColumn;
        			}
            	},
            	new Predicate<BlockCoord>() { public boolean test(BlockCoord coord) { /*---*/ return false; /*---*/ } });
    }
	
}
