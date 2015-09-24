package com.thecodewarrior.catwalks.item;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.thecodewarrior.catwalks.CatwalkMod;
import com.thecodewarrior.catwalks.block.BlockScaffold;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.catwalks.util.Predicate;
import com.thecodewarrior.codechicken.lib.vec.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockScaffold extends ItemBlock {

	Random rand = new Random();
	
	public ItemBlockScaffold(Block p_i45328_1_) {
		super(p_i45328_1_);
		this.setHasSubtypes(true);
	}
	
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.field_150939_a.getUnlocalizedName() + ( stack.getItemDamage() != 0 ? ".builder" : "" );
    }
    
    public int getMetadata(int damage)
    {
        return damage == 0 ? 0 : 1; // limit meta to 0 and 1
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int _side, float hitX, float hitY, float hitZ)
    {
    	
    	return CatwalkUtil.extendBlock(stack, player, world, x, y, z, _side, hitX, hitY, hitZ, this,
    	        new Predicate<Block>() { public boolean test(Block block) { /*---*/ return block instanceof BlockScaffold; /*---*/ } },
    	        new Predicate<BlockCoord>(world) {
    				public boolean test(BlockCoord coord) {
    					World world = (World)args[0];
    					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockScaffold;
    				}
    	    	},
    	    	new Predicate<BlockCoord>(world) {
    	    		public boolean test(BlockCoord coord) {
    	    			World world = (World)args[0];
    					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockScaffold;
    	    		}
    	    	});
    }
    
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack)
    {
    	return CatwalkUtil.canPlaceBlock(stack, player, world, x, y, z, side, this,
    			new Predicate<Block>() { public boolean test(Block block) { /*---*/ return block instanceof BlockScaffold; /*---*/ } },
    	        new Predicate<BlockCoord>(world) {
    				public boolean test(BlockCoord coord) {
    					World world = (World)args[0];
    					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockScaffold;
    				}
    	    	},
    	    	new Predicate<BlockCoord>(world) {
    	    		public boolean test(BlockCoord coord) {
    	    			World world = (World)args[0];
    					return world.getBlock(coord.x, coord.y, coord.z) instanceof BlockScaffold;
    	    		}
    	    	});
    }
	
}
