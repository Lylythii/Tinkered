package slimeknights.tconstruct.shared;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.item.ItemTinkerBook;
import slimeknights.tconstruct.library.ShapedFallbackRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.plugin.quark.QuarkPlugin;
import slimeknights.tconstruct.shared.block.BlockClearGlass;
import slimeknights.tconstruct.shared.block.BlockClearStainedGlass;
import slimeknights.tconstruct.shared.block.BlockGlow;
import slimeknights.tconstruct.shared.block.BlockMetal;
import slimeknights.tconstruct.shared.block.BlockOre;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.shared.block.BlockSlimeCongealed;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.shared.item.ItemMetaDynamicTinkers;
import slimeknights.tconstruct.shared.worldgen.NetherOreGenerator;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerCommons.PulseId, forced = true)
public class TinkerCommons extends TinkerPulse {

	public static final String PulseId = "TinkerCommons";
	static final Logger log = Util.getLogger(PulseId);

	@SidedProxy(clientSide = "slimeknights.tconstruct.shared.CommonsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
	public static CommonProxy proxy;

	public static BlockSoil blockSoil;
	public static BlockOre blockOre;
	public static BlockMetal blockMetal;
	public static BlockGlow blockGlow;

	public static BlockSlime blockSlime;
	public static BlockSlimeCongealed blockSlimeCongealed;

	// glass
	public static Block blockClearGlass;
	public static BlockClearStainedGlass blockClearStainedGlass;

	// block itemstacks
	public static ItemStack grout;
	public static ItemStack slimyMudGreen;
	public static ItemStack slimyMudBlue;
	public static ItemStack slimyMudMagma;
	public static ItemStack graveyardSoil;
	public static ItemStack consecratedSoil;

	public static ItemStack oreCobalt;
	public static ItemStack oreArdite;

	public static ItemStack blockCobalt;
	public static ItemStack blockArdite;
	public static ItemStack blockManyullyn;
	public static ItemStack blockPigIron;
	public static ItemStack blockKnightSlime;
	public static ItemStack blockSilkyJewel;
	public static ItemStack blockAlubrass;

	public static ItemStack lavawood;
	public static ItemStack firewood;

	// Items
	public static ItemTinkerBook book;
	public static ItemMetaDynamic nuggets;
	public static ItemMetaDynamic ingots;
	public static ItemMetaDynamic materials;
	public static ItemEdible edibles;

	//Knight Slime
	public static ItemStack ingotKnightSlime;
	public static ItemStack nuggetKnightSlime;

	// Material Itemstacks
	public static ItemStack searedBrick;

	public static ItemStack matSlimeBallBlue;
	public static ItemStack matSlimeBallPurple;
	public static ItemStack matSlimeBallBlood;
	public static ItemStack matSlimeBallMagma;
	public static ItemStack matSlimeBallPink;

	public static ItemStack matSlimeCrystalGreen;
	public static ItemStack matSlimeCrystalBlue;
	public static ItemStack matSlimeCrystalMagma;

	public static ItemStack matExpanderW;
	public static ItemStack matExpanderH;
	public static ItemStack matReinforcement;
	public static ItemStack matCreativeModifier;
	public static ItemStack matSilkyCloth;
	public static ItemStack matSilkyJewel;
	public static ItemStack matNecroticBone;
	public static ItemStack matMoss;
	public static ItemStack matMendingMoss;

	//Jerky
	public static ItemStack jerkyMonster;
	public static ItemStack jerkySpider;
	public static ItemStack jerkyBeef;
	public static ItemStack jerkyPork;
	public static ItemStack jerkyPoultry;
	public static ItemStack jerkyMutton;
	public static ItemStack jerkyBear;
	public static ItemStack jerkyCalamari;
	public static ItemStack jerkyHorse;
	public static ItemStack jerkyFowl;
	public static ItemStack jerkyVenison;
	public static ItemStack jerkyCanine;
	public static ItemStack jerkyRabbit;
	public static ItemStack jerkyMongoose;
	public static ItemStack jerkyGranFeline;
	public static ItemStack jerkyCamelid;

	public static ItemStack jerkyFish;
	public static ItemStack jerkySalmon;
	public static ItemStack jerkyClownfish;
	public static ItemStack jerkyPufferfish;

	//Slime Drop
	public static ItemStack slimedropGreen;
	public static ItemStack slimedropBlue;
	public static ItemStack slimedropPurple;
	public static ItemStack slimedropBlood;
	public static ItemStack slimedropMagma;


	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		boolean forced = Config.forceRegisterAll; // causes to always register all items

		// Soils
		blockSoil = registerBlock(registry, new BlockSoil(), "soil");

		// Slime Blocks
		// Quark plugin replaces this with one that works with the Quark colored slime feature
		if(!TConstruct.pulseManager.isPulseLoaded(QuarkPlugin.PulseId)) {
			blockSlime = registerBlock(registry, new BlockSlime(), "slime");
		}
		blockSlimeCongealed = registerBlock(registry, new BlockSlimeCongealed(), "slime_congealed");

		// Ores
		blockOre = registerBlock(registry, new BlockOre(), "ore");

		blockClearGlass = registerBlock(registry, new BlockClearGlass(), "clear_glass");
		blockClearStainedGlass = registerBlock(registry, new BlockClearStainedGlass(), "clear_stained_glass");

		// Ingots and nuggets
		if(isToolsLoaded() || isSmelteryLoaded() || forced) {
			blockMetal = registerBlock(registry, new BlockMetal(), "metal");
		}

		if(isToolsLoaded() || isGadgetsLoaded()) {
			blockGlow = registerBlock(registry, new BlockGlow(), "glow");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		boolean forced = Config.forceRegisterAll; // causes to always register all items

		book = registerItem(registry, new ItemTinkerBook(), "book");

		// Soils
		blockSoil = registerEnumItemBlock(registry, blockSoil);

		grout = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GROUT.getMeta());
		slimyMudGreen = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_GREEN.getMeta());
		slimyMudBlue = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_BLUE.getMeta());
		slimyMudMagma = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_MAGMA.getMeta());
		graveyardSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GRAVEYARD.getMeta());
		consecratedSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.CONSECRATED.getMeta());

		// Slime Blocks
		blockSlime = registerItemBlockProp(registry, new ItemBlockMeta(blockSlime), BlockSlime.TYPE);
		blockSlimeCongealed = registerItemBlockProp(registry, new ItemBlockMeta(blockSlimeCongealed), BlockSlime.TYPE);

		// Ores
		blockOre = registerEnumItemBlock(registry, blockOre);

		oreCobalt = new ItemStack(blockOre, 1, BlockOre.OreTypes.COBALT.getMeta());
		oreArdite = new ItemStack(blockOre, 1, BlockOre.OreTypes.ARDITE.getMeta());

		blockClearGlass = registerItemBlock(registry, blockClearGlass);
		blockClearStainedGlass = registerEnumItemBlock(registry, blockClearStainedGlass);

		// create the items. We can probably always create them since they handle themselves dynamically
		nuggets = registerItem(registry, new ItemMetaDynamicTinkers(), "nuggets");
		ingots = registerItem(registry, new ItemMetaDynamicTinkers(), "ingots");
		materials = registerItem(registry, new ItemMetaDynamic(), "materials");
		edibles = registerItem(registry, new ItemEdible(), "edible");

		nuggets.setCreativeTab(TinkerRegistry.tabGeneral);
		ingots.setCreativeTab(TinkerRegistry.tabGeneral);
		materials.setCreativeTab(TinkerRegistry.tabGeneral);
		edibles.setCreativeTab(TinkerRegistry.tabGeneral);

		// Items that can always be present.. slimeballs
		matSlimeBallBlue = edibles.addFood(1, 1, 1f, "slimeball_blue", new PotionEffect(MobEffects.SLOWNESS, 20 * 45, 2), new PotionEffect(MobEffects.JUMP_BOOST, 20 * 60, 2));
		matSlimeBallPurple = edibles.addFood(2, 1, 2f, "slimeball_purple", new PotionEffect(MobEffects.UNLUCK, 20 * 45), new PotionEffect(MobEffects.LUCK, 20 * 60));
		matSlimeBallBlood = edibles.addFood(3, 1, 1.5f, "slimeball_blood", new PotionEffect(MobEffects.POISON, 20 * 45, 2), new PotionEffect(MobEffects.HEALTH_BOOST, 20 * 60));
		matSlimeBallMagma = edibles.addFood(4, 2, 1f, "slimeball_magma", new PotionEffect(MobEffects.WEAKNESS, 20 * 45), new PotionEffect(MobEffects.WITHER, 20 * 15), new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 60));
		matSlimeBallPink = edibles.addFood(5, 1, 1f, "slimeball_pink", new PotionEffect(MobEffects.NAUSEA, 20 * 10, 2)); // you mixed how many types of slime for this? its gross

		// All other items are either ingots or items for modifiers

		if(isSmelteryLoaded() || forced) {
			searedBrick = materials.addMeta(0, "seared_brick");
		}

		// Ingots and nuggets
		if(isToolsLoaded() || isSmelteryLoaded() || forced) {

			blockMetal = registerEnumItemBlock(registry, blockMetal);

			blockCobalt = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.COBALT.getMeta());
			blockArdite = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.ARDITE.getMeta());
			blockManyullyn = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.MANYULLYN.getMeta());
			blockKnightSlime = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.KNIGHTSLIME.getMeta());
			blockPigIron = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.PIGIRON.getMeta());
			blockAlubrass = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.ALUBRASS.getMeta());
			blockSilkyJewel = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.SILKY_JEWEL.getMeta());
		}

		// Materials
		if(isToolsLoaded() || forced) {

			matSlimeCrystalGreen = materials.addMeta(9, "slimecrystal_green");
			matSlimeCrystalBlue = materials.addMeta(10, "slimecrystal_blue");
			matSlimeCrystalMagma = materials.addMeta(11, "slimecrystal_magma");
			matExpanderW = materials.addMeta(12, "expander_w");
			matExpanderH = materials.addMeta(13, "expander_h");
			matReinforcement = materials.addMeta(14, "reinforcement");

			matSilkyCloth = materials.addMeta(15, "silky_cloth");
			matSilkyJewel = materials.addMeta(16, "silky_jewel");

			matNecroticBone = materials.addMeta(17, "necrotic_bone");
			matMoss = materials.addMeta(18, "moss");
			matMendingMoss = materials.addMeta(19, "mending_moss");

			matCreativeModifier = materials.addMeta(50, "creative_modifier");

			ingotKnightSlime = ingots.addMeta(3, "knightslime");
			nuggetKnightSlime = nuggets.addMeta(3, "knightslime");
		}

		if(isGadgetsLoaded() || forced) {

			//Jerky
			jerkyMonster = edibles.addFood(10, 4, 0.4f, "jerky_monster", false);
			jerkySpider = edibles.addFood(11, 4, 0.4f, "jerky_spider", false);
			jerkyBeef = edibles.addFood(12, 8, 1f, "jerky_beef", false);
			jerkyPork = edibles.addFood(13, 8, 1f, "jerky_pork", false);
			jerkyPoultry = edibles.addFood(14, 6, 0.8f, "jerky_poultry", false);
			jerkyMutton = edibles.addFood(15, 6, 1f, "jerky_mutton", false);
			jerkyBear = edibles.addFood(16, 5, 0.8f, "jerky_bear", false);
			jerkyCalamari = edibles.addFood(17, 5, 0.8f, "jerky_calamari", false);
			jerkyHorse = edibles.addFood(18, 5, 0.8f, "jerky_horse", false);
			jerkyFowl = edibles.addFood(19, 5, 0.8f, "jerky_fowl", false);
			jerkyVenison = edibles.addFood(20, 5, 0.8f, "jerky_venison", false);
			jerkyCanine = edibles.addFood(21, 5, 0.8f, "jerky_canine", false);
			jerkyRabbit = edibles.addFood(22, 5, 0.8f, "jerky_rabbit", false);
			jerkyMongoose = edibles.addFood(23, 5, 0.8f, "jerky_mongoose", false);
			jerkyGranFeline = edibles.addFood(24, 5, 0.8f, "jerky_gran_feline", false);
			jerkyCamelid = edibles.addFood(25, 5, 0.8f, "jerky_camelid", false);

			jerkyFish = edibles.addFood(26, 5, 0.8f, "jerky_fish", false);
			jerkySalmon = edibles.addFood(27, 6, 1f, "jerky_salmon", false);
			jerkyClownfish = edibles.addFood(28, 3, 0.8f, "jerky_clownfish", false);
			jerkyPufferfish = edibles.addFood(29, 3, 0.8f, "jerky_pufferfish", false);

			//Slime Drops
			slimedropGreen = edibles.addFood(30, 1, 1f, "slimedrop_green", new PotionEffect(MobEffects.SPEED, 20 * 90, 2));
			slimedropBlue = edibles.addFood(31, 3, 1f, "slimedrop_blue", new PotionEffect(MobEffects.JUMP_BOOST, 20 * 90, 2));
			slimedropPurple = edibles.addFood(32, 3, 2f, "slimedrop_purple", new PotionEffect(MobEffects.LUCK, 20 * 90));
			slimedropBlood = edibles.addFood(33, 3, 1.5f, "slimedrop_blood", new PotionEffect(MobEffects.HEALTH_BOOST, 20 * 90));
			slimedropMagma = edibles.addFood(34, 6, 1f, "slimedrop_magma", new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 90));
		}
	}

	@SubscribeEvent
	public void registerRecipes(Register<IRecipe> event) {
		// replace the vanilla slimeblock recipe with one that does not conflict with our slimeblocks
		CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped("###", "###", "###", '#', "slimeball");
		Ingredient[] ignore = new Ingredient[] {
				Config.matchVanillaSlimeblock ? new OreIngredient("slimeballGreen") : Ingredient.fromStacks(matSlimeBallPink.copy()),
				new OreIngredient("slimeballBlue"),
				new OreIngredient("slimeballPurple"),
				new OreIngredient("slimeballBlood"),
				new OreIngredient("slimeballMagma")
		};
		// if enabled, mixing slimeballs gives you pink
		ItemStack output = Config.matchVanillaSlimeblock ? new ItemStack(blockSlime, 1, BlockSlime.SlimeType.PINK.meta) : new ItemStack(Blocks.SLIME_BLOCK);
		ShapedFallbackRecipe recipe = new ShapedFallbackRecipe(Util.getResource("slime_blocks"), output, primer, ignore, 9);
		recipe.setRegistryName("minecraft:slime");
		event.getRegistry().register(recipe);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		proxy.registerModels();
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		registerSmeltingRecipes();
		proxy.init();

		GameRegistry.registerWorldGenerator(NetherOreGenerator.INSTANCE, 0);

		MinecraftForge.EVENT_BUS.register(new AchievementEvents());
		MinecraftForge.EVENT_BUS.register(new BlockEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
	}

	// POST-INITIALIZATION
	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		TinkerRegistry.tabGeneral.setDisplayIcon(matSlimeBallBlue);
	}

	private void registerSmeltingRecipes() {
		GameRegistry.addSmelting(graveyardSoil, consecratedSoil, 0.1f);

		if(!isSmelteryLoaded()) {
			// compat recipe if the smeltery is not available for melting
			GameRegistry.addSmelting(Blocks.GLASS, new ItemStack(blockClearGlass), 0.1f);
		}
	}
}
