package com.thecodewarrior.catwalks.legacy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.thecodewarrior.catwalks.CatwalkMod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "catwalkmod", version = "x.x.x", dependencies="required-after:catwalks")
public class LegacyCatwalkMod {
	
    public static Block LEGACY_catwalk;
    public static Block LEGACY_ladder;
    
    public static Item lightRopeLegacy;
    public static Item blowtorchLegacy;
    public static Item steelGrateLegacy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	
    	LEGACY_catwalk = new BlockLegacyCatwalk();
    	LEGACY_ladder  = new BlockLegacyCagedLadder();
    	
    	GameRegistry.registerBlock(LEGACY_catwalk, ItemBlockLegacy.class, "scaffold");
    	GameRegistry.registerBlock(LEGACY_ladder , ItemBlockLegacy.class, "cagedLadder");
    	
    	lightRopeLegacy  = new ItemLegacy("item.legacy.lightrope" , "catwalkmod:light_rope" );
    	blowtorchLegacy  = new ItemLegacy("item.legacy.blowtorch" , "catwalkmod:blowtorch"  );
    	steelGrateLegacy = new ItemLegacy("item.legacy.steelgrate", "catwalkmod:steel_grate");
    	
    	GameRegistry.registerItem(lightRopeLegacy , "ropeLight" );
    	GameRegistry.registerItem(blowtorchLegacy , "blowtorch" );
    	GameRegistry.registerItem(steelGrateLegacy, "steelGrate");
    	
    	GameRegistry.registerTileEntity(TileEntityCatwalk.class, "catwalkmod:tileEntityScaffold");
		GameRegistry.registerTileEntity(TileEntityCagedLadder.class, "catwalkmod:tileEntityCagedLadder");
		
		registerLegacyRecipe(Item.getItemFromBlock(LEGACY_catwalk), Item.getItemFromBlock(CatwalkMod.defaultCatwalk));
		registerLegacyRecipe(Item.getItemFromBlock(LEGACY_ladder), Item.getItemFromBlock(CatwalkMod.defaultLadder));
		
		registerLegacyRecipe(lightRopeLegacy, CatwalkMod.itemRopeLight);
		registerLegacyRecipe(blowtorchLegacy, CatwalkMod.itemBlowtorch);
//		registerLegacyRecipe(steelGrateLegacy, ****);
    }
    
    public void registerLegacyRecipe(Item oldItem, Item newItem) {
    	GameRegistry.addShapelessRecipe(new ItemStack(newItem, 1), new ItemStack(oldItem, 1));
    }

}
