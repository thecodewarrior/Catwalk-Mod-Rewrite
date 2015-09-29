package com.thecodewarrior.catwalks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thecodewarrior.catwalks.block.BlockCagedLadder;
import com.thecodewarrior.catwalks.block.BlockCatwalk;
import com.thecodewarrior.catwalks.block.BlockScaffold;
import com.thecodewarrior.catwalks.block.BlockSturdyRail;
import com.thecodewarrior.catwalks.block.BlockSturdyRailActivator;
import com.thecodewarrior.catwalks.block.BlockSturdyRailBooster;
import com.thecodewarrior.catwalks.block.BlockSturdyRailDetector;
import com.thecodewarrior.catwalks.block.BlockSupportColumn;
import com.thecodewarrior.catwalks.item.ItemBlockScaffold;
import com.thecodewarrior.catwalks.item.ItemBlockSupportColumn;
import com.thecodewarrior.catwalks.item.ItemBlowtorch;
import com.thecodewarrior.catwalks.item.ItemCautionTape;
import com.thecodewarrior.catwalks.item.ItemRopeLight;
import com.thecodewarrior.catwalks.item.ItemSteelGrate;
import com.thecodewarrior.catwalks.util.CatwalkOptions;
import com.thecodewarrior.catwalks.util.CatwalkUtil;
import com.thecodewarrior.catwalks.util.CommonProxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid=CatwalkMod.MODID, name=CatwalkMod.MODNAME, version=CatwalkMod.MODVER)
public class CatwalkMod
{
    public static final String MODID = "catwalks";
    public static final String MODNAME = "Catwalks";
    public static final String MODVER = "2.0.2";
    
	public static final String loggerName = "Catwalks";
	public static Logger l;
	
	public static Logger logger(String... name) {
		if(name.length > 0) {
			return LogManager.getLogger(loggerName + "][" + StringUtils.join(name, "]["));
		} else {
			return LogManager.getLogger(loggerName);
		}
	}
    
    @Instance(value = CatwalkMod.MODID)
    public static CatwalkMod instance;
    
    public static CatwalkOptions options = new CatwalkOptions();
        
    public static CreativeTabs catwalkTab;
    
    public static Block.SoundType catwalkSounds;
    public static Block.SoundType ladderSounds;
    
    public static Block sturdyTrack;
    public static Block sturdyPoweredTrack;
    public static Block sturdyDetectorTrack;
    public static Block sturdyActivatorTrack;
    
    public static Block supportColumn;
    public static Block scaffold;
    
    public static Block defaultCatwalk;
    /**
     * Usage: <pre> catwalks.get(lights).get(isBottomOpen).get(tape); </pre>
     */
    public static Map<Boolean,         // lights
    				  Map<Boolean,     // bottom
    				      Map<Boolean, // tape
    				          Block>>> catwalks;
    
    public static Block defaultLadder;
    /**
     * Usage: <pre> ladders.get(facing).get(lights).get(isBottomOpen).get(tape); </pre>
     */
    public static Map<ForgeDirection,      // facing
    				  Map<Boolean,         // light
    				      Map<Boolean,     // bottom
    				          Map<Boolean, // tape
    				          	  Block>>>> ladders;
    
    public static Item itemBlowtorch;
    public static Item itemRopeLight;
    public static Item itemCautionTape;
    public static Item itemSteelGrate;
    
    public static int speedEffectLevel = 2;
    public static AttributeModifier speedModifier;
    
    @SidedProxy(clientSide="com.thecodewarrior.catwalks.util.ClientProxy", serverSide="com.thecodewarrior.catwalks.util.CommonProxy")
    public static CommonProxy proxy;
    
    public static int catwalkRenderType;
    public static int ladderRenderType;
    public static int inAndOutRenderType;
    public static int supportRenderType;
    
	public static int lightLevel = 12;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	l = logger();
    	CatwalkUtil.init();
    	
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	
    	options.init();
    	options.load(config);
    	
    	catwalkTab = new CreativeTabs("catwalks") {
    		@Override
    		@SideOnly(Side.CLIENT)
    		public Item getTabIconItem() {
    			return Item.getItemFromBlock(CatwalkMod.scaffold);
    		}
    	};
    	
    	catwalkRenderType   = RenderingRegistry.getNextAvailableRenderId();
    	ladderRenderType    = RenderingRegistry.getNextAvailableRenderId();
    	inAndOutRenderType  = RenderingRegistry.getNextAvailableRenderId();
    	supportRenderType   = RenderingRegistry.getNextAvailableRenderId();
    	
    	catwalkSounds = Block.soundTypeMetal;
    	ladderSounds  = Block.soundTypeLadder;
    	
    	FMLCommonHandler.instance().bus().register(proxy);  
    	MinecraftForge.EVENT_BUS.register(proxy);
    	speedModifier =  new AttributeModifier(
    			"catwalkmod.speedup",
    			0.20000000298023224D,
    			2);
    	speedModifier.setSaved(false);
    	
    	
    	registerItems();
    	
    }
    
    public void registerItems() {
    	boolean[] trueFalse = {true, false};
    	
    	sturdyTrack = new BlockSturdyRail();
    	GameRegistry.registerBlock(sturdyTrack, "sturdy_rail");
    	
    	sturdyPoweredTrack = new BlockSturdyRailBooster();
    	GameRegistry.registerBlock(sturdyPoweredTrack, "sturdy_rail_powered");
    	
    	sturdyDetectorTrack = new BlockSturdyRailDetector();
    	GameRegistry.registerBlock(sturdyDetectorTrack, "sturdy_rail_detector");
    	
    	sturdyActivatorTrack = new BlockSturdyRailActivator();
    	GameRegistry.registerBlock(sturdyActivatorTrack, "sturdy_rail_activator");
    	
    	
    	supportColumn = new BlockSupportColumn();
    	GameRegistry.registerBlock(supportColumn, ItemBlockSupportColumn.class, "support_column");
    	scaffold = new BlockScaffold();
    	GameRegistry.registerBlock(scaffold, ItemBlockScaffold.class, "scaffold");
    	
    	catwalks = new HashMap<Boolean, Map<Boolean, Map<Boolean,Block>>>();
    	for(boolean lights : trueFalse) {
    		Map<Boolean, Map<Boolean, Block>> lightMap = new HashMap<Boolean, Map<Boolean,Block>>();
    		catwalks.put(lights, lightMap);
    		for(boolean bottom : trueFalse) {
    			Map<Boolean, Block> bottomMap = new HashMap<Boolean,Block>();
        		lightMap.put(bottom, bottomMap);
    			for(boolean tape : trueFalse) {
    				BlockCatwalk b = new BlockCatwalk(lights, bottom, tape);
    				bottomMap.put(tape, b);
    				String id = "catwalk";
    				id += lights ? "_lit" : "_unlit";
    				if(!bottom) id += "_nobottom";
    				if(tape) id += "_tape";
    				GameRegistry.registerBlock(b, id);
    			}
    		}
    	}
    	defaultCatwalk = catwalks.get(false).get(true).get(false);
    	
    	ForgeDirection[] directions = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
    	ladders = new HashMap<ForgeDirection, Map<Boolean,Map<Boolean,Map<Boolean,Block>>>>();
    	for (ForgeDirection dir : directions) {
			Map<Boolean, Map<Boolean, Map<Boolean, Block>>> dirMap = new HashMap<Boolean, Map<Boolean,Map<Boolean,Block>>>();
			ladders.put(dir, dirMap);
			
			for (boolean lights : trueFalse) {
				Map<Boolean, Map<Boolean, Block>> lightMap = new HashMap<Boolean, Map<Boolean,Block>>();
				dirMap.put(lights, lightMap);
				
				for (boolean bottomOpen : trueFalse) {
					Map<Boolean, Block> bottomMap = new HashMap<Boolean, Block>();
					lightMap.put(bottomOpen, bottomMap);
					
					for (boolean tape : trueFalse) {
						BlockCagedLadder b = new BlockCagedLadder(dir, lights, bottomOpen, tape);
						bottomMap.put(tape, b);
						String id = "cagedLadder";
						if(dir == ForgeDirection.NORTH) id += "_north";
						if(dir == ForgeDirection.SOUTH) id += "_south";
						if(dir == ForgeDirection.WEST)  id += "_west";
						if(dir == ForgeDirection.EAST)  id += "_east";
						if(lights) id += "_lit"; else id += "_unlit";
						if(bottomOpen) id += "_nobottom";
						if(tape) id += "_tape";
//						System.out.println("Registered block: " + id);
				    	GameRegistry.registerBlock(b, id);
					}
				}
			}
		}
    	defaultLadder = ladders.get(ForgeDirection.NORTH).get(false).get(false).get(false);
    	
    	itemBlowtorch   = new ItemBlowtorch();
    	itemRopeLight   = new ItemRopeLight();
    	itemCautionTape = new ItemCautionTape();
    	itemSteelGrate  = new ItemSteelGrate();
    	
    	GameRegistry.registerItem(itemBlowtorch,   "blowtorch"  );
    	GameRegistry.registerItem(itemRopeLight,   "ropeLight"  );
    	GameRegistry.registerItem(itemCautionTape, "cautionTape");
    	GameRegistry.registerItem(itemSteelGrate,  "steelgrate" );
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
    	
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBlowtorch), new Object[] {
    		"FXX",
    		"XIX",
    		"XXI",
    		'F', Items.flint_and_steel,
    		'I', "ingotIron"
    	}));
    	
    	GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemCautionTape, 16), "dyeYellow", "dyeBlack", "slimeball"));
    	
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRopeLight, 8), new Object[]{
    		"GSG",
    		'G', "dustGlowstone",
    		'S', Items.string
    	}));

        if (options.altGrateRecipe) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemSteelGrate, 16), new Object[] {
                "IXI",
                "BBB",
                "IXI",
                'I', "ingotIron",
                'B', Blocks.iron_bars
            }));
        }
        else {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemSteelGrate, 16), new Object[] {
                "IXI",
                "XIX",
                "IXI",
                'I', "ingotIron"
            }));
        }
    	
    	GameRegistry.addRecipe(new ItemStack(defaultCatwalk, 3), new Object[] {
    		"GXG",
    		"XGX",
    		'G', itemSteelGrate
    	});
    	
    	GameRegistry.addRecipe(new ItemStack(defaultLadder, 3), new Object[] {
    		"GLG",
    		"XGX",
    		'G', itemSteelGrate,
    		'L', Item.getItemFromBlock(Blocks.ladder)
    	});
    	
    	GameRegistry.addRecipe(new ItemStack(scaffold, 4), new Object[] {
    		"GG",
    		"GG",
    		'G', itemSteelGrate
    	});
    	GameRegistry.addShapelessRecipe(new ItemStack(scaffold, 1, 1), new ItemStack(scaffold, 1, 0));
    	GameRegistry.addShapelessRecipe(new ItemStack(scaffold, 1, 0), new ItemStack(scaffold, 1, 1));

    	GameRegistry.addRecipe(new ItemStack(supportColumn, 4), new Object[] {
    		"G",
    		"G",
    		'G', itemSteelGrate
    	});

    	GameRegistry.addShapelessRecipe(new ItemStack(sturdyTrack, 1), new Object[] { itemSteelGrate, Blocks.rail } );
    	GameRegistry.addShapelessRecipe(new ItemStack(sturdyPoweredTrack, 1), new Object[] { itemSteelGrate, Blocks.golden_rail } );
    	GameRegistry.addShapelessRecipe(new ItemStack(sturdyDetectorTrack, 1), new Object[] { itemSteelGrate, Blocks.detector_rail } );
    	GameRegistry.addShapelessRecipe(new ItemStack(sturdyActivatorTrack, 1), new Object[] { itemSteelGrate, Blocks.activator_rail } );

    	proxy.init();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit();
    	options.postInit();
    }
}