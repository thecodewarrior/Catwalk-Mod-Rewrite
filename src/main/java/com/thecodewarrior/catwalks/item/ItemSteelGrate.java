package com.thecodewarrior.catwalks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.thecodewarrior.catwalks.CatwalkMod;

public class ItemSteelGrate extends Item {
	public ItemSteelGrate() {
		setUnlocalizedName("steel_grate");
		setCreativeTab(CatwalkMod.catwalkTab);
		setTextureName(CatwalkMod.MODID + ":steelgrate");
	}
}
