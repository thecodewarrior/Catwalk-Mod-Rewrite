package com.thecodewarrior.catwalks;

import java.util.Set;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;

import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlowtorch extends Item implements IToolWrench{
	
	public ItemBlowtorch() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("blowtorch");
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(CatwalkMod.MODID + ":" + "blowtorch");
    }
	
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }
	
	@Override
	public Set<String> getToolClasses(ItemStack stack) {
	    return ImmutableSet.of("wrench");
	}

	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {
		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {}
}
