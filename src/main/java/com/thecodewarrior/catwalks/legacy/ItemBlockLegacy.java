package com.thecodewarrior.catwalks.legacy;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockLegacy extends ItemBlock {
	
	public String name;
	
	public ItemBlockLegacy(Block b) {
		super(b);
		name = b.getUnlocalizedName();
	}
	
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world,
    		int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
    	return false;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
    	String s;
    	s = StatCollector.translateToLocalFormatted("item.legacy.lore.0", (StatCollector.translateToLocal(name + ".name")).trim() ).trim();
    	if(!s.startsWith("item.legacy"))
    		list.add(s);
    	s = StatCollector.translateToLocalFormatted("item.legacy.lore.1", (StatCollector.translateToLocal(name + ".name")).trim() ).trim();
    	if(!s.startsWith("item.legacy"))
    		list.add(s);
    	s = StatCollector.translateToLocalFormatted("item.legacy.lore.2", (StatCollector.translateToLocal(name + ".name")).trim() ).trim();
    	if(!s.startsWith("item.legacy"))
    		list.add(s);
    }
    
    public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        return StatCollector.translateToLocalFormatted("item.legacy.name", (StatCollector.translateToLocal(name + ".name")).trim() ).trim();
    }
	
}
