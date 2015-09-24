package com.thecodewarrior.catwalks;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

import com.thecodewarrior.catwalks.legacy.LegacyCatwalkMod;

public class NEICatwalksConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		for (Map<Boolean, Map<Boolean, Block>> m : CatwalkMod.catwalks.values()) {
			for (Map<Boolean, Block> m2 : m.values()) {
				for (Block b : m2.values()) {
					if(b == CatwalkMod.defaultCatwalk)
						continue;
					API.hideItem(new ItemStack(b));
				}
			}
		}
		for (Map<Boolean, Map<Boolean, Map<Boolean, Block>>> m : CatwalkMod.ladders.values()) {
			for (Map<Boolean, Map<Boolean, Block>> m2 : m.values()) {
				for (Map<Boolean, Block> m3 : m2.values()) {
					for(Block b : m3.values()) {
						if(b == CatwalkMod.defaultLadder)
							continue;
						API.hideItem(new ItemStack(b));
					}
				}
			}
		}
		API.hideItem(new ItemStack(LegacyCatwalkMod.blowtorchLegacy));
		API.hideItem(new ItemStack(LegacyCatwalkMod.LEGACY_catwalk));
		API.hideItem(new ItemStack(LegacyCatwalkMod.LEGACY_ladder));
		API.hideItem(new ItemStack(LegacyCatwalkMod.lightRopeLegacy));
		API.hideItem(new ItemStack(LegacyCatwalkMod.steelGrateLegacy));
	}

	@Override
	public String getName() {
		return CatwalkMod.MODNAME;
	}

	@Override
	public String getVersion() {
		return CatwalkMod.MODVER;
	}

}
