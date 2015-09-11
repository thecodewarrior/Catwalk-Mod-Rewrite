package com.thecodewarrior.catwalks.legacy;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemLegacy extends Item {

	public String name;
	
	public ItemLegacy(String name, String texture) {
		this.name = name;
		this.setTextureName(texture);
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
