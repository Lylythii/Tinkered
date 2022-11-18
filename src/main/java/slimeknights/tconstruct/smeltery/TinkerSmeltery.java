package slimeknights.tconstruct.smeltery;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import net.dries007.tfc.objects.fluids.FluidsTFC;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.BucketCastingRecipe;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.smeltery.PreferenceCastingRecipe;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockChannel;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSearedGlass;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryIO;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.block.BlockTinkerTankController;
import slimeknights.tconstruct.smeltery.item.CastCustom;
import slimeknights.tconstruct.smeltery.item.ItemChannel;
import slimeknights.tconstruct.smeltery.item.ItemTank;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;
import slimeknights.tconstruct.tools.TinkerMaterials;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Pulse(id = TinkerSmeltery.PulseId, description = "The smeltery and items needed for it")
public class TinkerSmeltery extends TinkerPulse {

  public static final String PulseId = "TinkerSmeltery";
  public static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.smeltery.SmelteryClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static BlockSeared searedBlock;
  public static BlockSmelteryController smelteryController;
  public static BlockTank searedTank;
  public static BlockFaucet faucet;
  public static BlockChannel channel;
  public static BlockCasting castingBlock;
  public static BlockSmelteryIO smelteryIO;
  public static BlockSearedGlass searedGlass;

  public static Block tinkerTankController;

  // Items
  public static Cast cast;
  public static CastCustom castCustom;
  public static Cast clayCast;

  // itemstacks!
  public static ItemStack castIngot;
  public static ItemStack castNugget;
  public static ItemStack castGem;
  public static ItemStack castShard;
  public static ItemStack castPlate;
  public static ItemStack castGear;

  private static Map<Fluid, Set<Pair<String, Integer>>> knownOreFluids = Maps.newHashMap();
  public static List<FluidStack> castCreationFluids = Lists.newLinkedList();
  public static List<FluidStack> clayCreationFluids = Lists.newLinkedList();

  public static ImmutableSet<Block> validSmelteryBlocks;
  public static ImmutableSet<Block> validTinkerTankBlocks;
  public static ImmutableSet<Block> validTinkerTankFloorBlocks;
  public static List<ItemStack> meltingBlacklist = Lists.newLinkedList();

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    searedBlock = registerBlock(registry, new BlockSeared(), "seared");
    smelteryController = registerBlock(registry, new BlockSmelteryController(), "smeltery_controller");
    searedTank = registerBlock(registry, new BlockTank(), "seared_tank");
    faucet = registerBlock(registry, new BlockFaucet(), "faucet");
    channel = registerBlock(registry, new BlockChannel(), "channel");
    castingBlock = registerBlock(registry, new BlockCasting(), "casting");
    smelteryIO = registerBlock(registry, new BlockSmelteryIO(), "smeltery_io");
    searedGlass = registerBlock(registry, new BlockSearedGlass(), "seared_glass");

    tinkerTankController = registerBlock(registry, new BlockTinkerTankController(), "tinker_tank_controller");

    registerTE(TileSmeltery.class, "smeltery_controller");
    registerTE(TileSmelteryComponent.class, "smeltery_component");
    registerTE(TileTank.class, "tank");
    registerTE(TileFaucet.class, "faucet");
    registerTE(TileChannel.class, "channel");
    registerTE(TileCastingTable.class, "casting_table");
    registerTE(TileCastingBasin.class, "casting_basin");
    registerTE(TileDrain.class, "smeltery_drain");
    registerTE(TileTinkerTank.class, "tinker_tank");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    searedBlock = registerEnumItemBlock(registry, searedBlock);
    smelteryController = registerItemBlock(registry, smelteryController);
    searedTank = registerItemBlockProp(registry, new ItemTank(searedTank), BlockTank.TYPE);
    faucet = registerItemBlock(registry, faucet);
    channel = registerItemBlock(registry, new ItemChannel(channel));
    castingBlock = registerItemBlockProp(registry, new ItemBlockMeta(castingBlock), BlockCasting.TYPE);
    smelteryIO = registerEnumItemBlock(registry, smelteryIO);
    searedGlass = registerEnumItemBlock(registry, searedGlass);

    tinkerTankController = registerItemBlock(registry, tinkerTankController);

    cast = registerItem(registry, new Cast(), "cast");
    castCustom = registerItem(registry, new CastCustom(), "cast_custom");
    castGear = castCustom.addMeta(4, "gear", Material.VALUE_Gear);
    castGem = castCustom.addMeta(2, "gem", Material.VALUE_Gem);
    castIngot = castCustom.addMeta(0, "ingot", Material.VALUE_Ingot);
    castNugget = castCustom.addMeta(1, "nugget", Material.VALUE_Nugget);
    castPlate = castCustom.addMeta(3, "plate", Material.VALUE_Plate);
    clayCast = registerItem(registry, new Cast(), "clay_cast");

    if(TinkerRegistry.getShard() != null) {
      TinkerRegistry.addCastForItem(TinkerRegistry.getShard());
      castShard = new ItemStack(cast);
      Cast.setTagForPart(castShard, TinkerRegistry.getShard());
    }
    
    // smeltery blocks
    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(searedBlock);
    builder.add(searedTank);
    builder.add(smelteryIO);
    builder.add(searedGlass);

    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build(); // same blocks right now
    validTinkerTankFloorBlocks = ImmutableSet.of(searedBlock, searedGlass, smelteryIO);

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.add(searedBlock);
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    // done here so they're present for integration in MaterialIntegration and fluids in TinkerFluids are also initialized
    castCreationFluids.add(new FluidStack(TinkerFluids.gold, Material.VALUE_Ingot * 2));

    // always add extra fluids, as we are not sure if they are integrated until the end of postInit and we added recipes using them before integration
    castCreationFluids.add(new FluidStack(TinkerFluids.brass, Material.VALUE_Ingot));
    castCreationFluids.add(new FluidStack(TinkerFluids.alubrass, Material.VALUE_Ingot));

    // add clay casts if enabled
    if(Config.claycasts) {
      clayCreationFluids.add(new FluidStack(TinkerFluids.clay, Material.VALUE_Ingot * 2));
    }

    registerSmelting();

    proxy.init();
  }

  private void registerSmelting() {
    GameRegistry.addSmelting(TinkerCommons.grout, TinkerCommons.searedBrick, 0.4f);

    GameRegistry.addSmelting(new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK.getMeta()), new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_CRACKED.getMeta()), 0.1f);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerSmelteryFuel();
    registerMeltingCasting();

    // register remaining cast creation
    for(FluidStack fs : castCreationFluids) {
      TinkerRegistry.registerTableCasting(new ItemStack(cast), ItemStack.EMPTY, fs.getFluid(), fs.amount);
      TinkerRegistry.registerTableCasting(new CastingRecipe(castGem, RecipeMatch.of("gemEmerald"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of("ingotBrick"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of("ingotBrickNether"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, new RecipeMatch.Item(TinkerCommons.searedBrick, 1), fs, true, true));
    }

    proxy.postInit();
    TinkerRegistry.tabSmeltery.setDisplayIcon(new ItemStack(searedTank));
  }

  private void registerSmelteryFuel() {
    TinkerRegistry.registerSmelteryFuel(new FluidStack(FluidRegistry.LAVA, 50), 100);
  }

  private void registerMeltingCasting() {
    // used in several places to register fluids for the crafting recipe scan
    ImmutableSet.Builder<Pair<String, Integer>> builder;
    int bucket = Fluid.BUCKET_VOLUME;

    // bucket casting
    TinkerRegistry.registerTableCasting(new BucketCastingRecipe(Items.BUCKET));

    // Water
    if(TConstruct.instance.tfc) {
      Fluid water = FluidsTFC.FRESH_WATER.get();
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.ICE, bucket), water, 305));
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.PACKED_ICE, bucket * 2), water, 310));
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.SNOW, bucket), water, 305));
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Items.SNOWBALL, bucket / 8), water, 301));
    } else {
      Fluid water = FluidRegistry.WATER;
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.ICE, bucket), water, 305));
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.PACKED_ICE, bucket * 2), water, 310));
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.SNOW, bucket), water, 305));
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Items.SNOWBALL, bucket / 8), water, 301));
    }

    // bloooooood
    TinkerRegistry.registerMelting(Items.ROTTEN_FLESH, TinkerFluids.blood, 40);
    if(TinkerCommons.matSlimeBallBlood != null) {
      TinkerRegistry.registerTableCasting(TinkerCommons.matSlimeBallBlood.copy(), ItemStack.EMPTY, TinkerFluids.blood, 160);
    }

    // purple slime
    TinkerRegistry.registerMelting(TinkerCommons.matSlimeBallPurple, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall);
    ItemStack slimeblock = new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.PURPLE.meta);
    TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 4);
    slimeblock = new ItemStack(TinkerCommons.blockSlime, 1, BlockSlime.SlimeType.PURPLE.meta);
    TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 9);

    // seared stone, takes as long as a full block to melt, but gives less
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("stone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore()));
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("cobblestone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore()));

    // obsidian
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("obsidian", Material.VALUE_Ore()),
                                                           TinkerFluids.obsidian, Material.VALUE_Ore()));
    // note that obsidian casting gives you 2 ingot value per obsidian, while part crafting only gives 1 per obsidian
    registerToolpartMeltingCasting(TinkerMaterials.obsidian);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.OBSIDIAN), ItemStack.EMPTY, TinkerFluids.obsidian, Material.VALUE_Ore());

    // gold is integrated via MaterialIntegration in TinkerIntegration now

    // special melting
    TinkerRegistry.registerMelting(Items.IRON_HORSE_ARMOR, TinkerFluids.cast_iron, Material.VALUE_Ingot * 4);
    TinkerRegistry.registerMelting(Items.GOLDEN_HORSE_ARMOR, TinkerFluids.gold, Material.VALUE_Ingot * 4);

    // rails, some of these are caught through registerOredictMelting, but for consistency all are just registered here
    TinkerRegistry.registerMelting(Blocks.RAIL, TinkerFluids.cast_iron, Material.VALUE_Ingot * 6 / 10);
    TinkerRegistry.registerMelting(Blocks.ACTIVATOR_RAIL, TinkerFluids.cast_iron, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(Blocks.DETECTOR_RAIL, TinkerFluids.cast_iron, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(Blocks.GOLDEN_RAIL, TinkerFluids.gold, Material.VALUE_Ingot);

    // register stone toolpart melting
    for(IToolPart toolPart : TinkerRegistry.getToolParts()) {
      if(toolPart.canBeCasted()) {
        if(toolPart instanceof MaterialItem) {
          ItemStack stack = toolPart.getItemstackWithMaterial(TinkerMaterials.stone);
          TinkerRegistry.registerMelting(MeltingRecipe.forAmount(
              RecipeMatch.ofNBT(stack, (toolPart.getCost() * Material.VALUE_SearedMaterial) / Material.VALUE_Ingot),
              TinkerFluids.searedStone, (int)(toolPart.getCost() * Config.oreToIngotRatio)));
        }
      }
    }

    // seared block casting and melting
    ItemStack blockSeared = new ItemStack(searedBlock);
    blockSeared.setItemDamage(BlockSeared.SearedType.STONE.getMeta());
    TinkerRegistry.registerTableCasting(TinkerCommons.searedBrick, castIngot, TinkerFluids.searedStone, Material.VALUE_SearedMaterial);
    TinkerRegistry.registerBasinCasting(blockSeared, ItemStack.EMPTY, TinkerFluids.searedStone, Material.VALUE_SearedBlock);

    ItemStack searedCobble = new ItemStack(searedBlock, 1, BlockSeared.SearedType.COBBLE.getMeta());
    TinkerRegistry.registerBasinCasting(new CastingRecipe(searedCobble, RecipeMatch.of("cobblestone"), TinkerFluids.searedStone, Material.VALUE_SearedBlock - Material.VALUE_SearedMaterial, true, false));


    // seared glass convenience recipe
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(searedGlass, 1, BlockSearedGlass.GlassType.GLASS.getMeta()),
                                                          RecipeMatch.of("blockGlass"),
                                                          new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial * 4),
                                                          true, true));

    // basically a pseudo-oredict of the seared blocks to support wildcard value
    TinkerRegistry.registerMelting(searedBlock, TinkerFluids.searedStone, Material.VALUE_SearedBlock);
    TinkerRegistry.registerMelting(TinkerCommons.searedBrick, TinkerFluids.searedStone, Material.VALUE_SearedMaterial);
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of(TinkerCommons.grout, Material.VALUE_SearedMaterial), TinkerFluids.searedStone, Material.VALUE_SearedMaterial / 3));


    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.HARDENED_CLAY), ItemStack.EMPTY, TinkerFluids.clay, Material.VALUE_BrickBlock);
    // funny thing about hardened clay. If it's stained and you wash it with water, it turns back into regular hardened clay!
    if(TConstruct.instance.tfc) {
          TinkerRegistry.registerBasinCasting(new CastingRecipe(
          new ItemStack(Blocks.HARDENED_CLAY),
          RecipeMatch.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE)),
          new FluidStack(FluidsTFC.FRESH_WATER.get(), 250),
          150,
          true,
          false));
    } else {
          TinkerRegistry.registerBasinCasting(new CastingRecipe(
          new ItemStack(Blocks.HARDENED_CLAY),
          RecipeMatch.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE)),
          new FluidStack(FluidRegistry.WATER, 250),
          150,
          true,
          false));
    }

    // emerald melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("oreEmerald", (int) (Material.VALUE_Gem * Config.oreToIngotRatio)), TinkerFluids.emerald));
    builder = ImmutableSet.builder();
    builder.add(Pair.of("gemEmerald", Material.VALUE_Gem));
    builder.add(Pair.of("blockEmerald", Material.VALUE_Gem * 9));
    addKnownOreFluid(TinkerFluids.emerald, builder.build());

    TinkerRegistry.registerTableCasting(new ItemStack(Items.EMERALD), castGem, TinkerFluids.emerald, Material.VALUE_Gem);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.EMERALD_BLOCK), ItemStack.EMPTY, TinkerFluids.emerald, Material.VALUE_Gem * 9);

    // glass melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("sand", Material.VALUE_Glass), TinkerFluids.glass));
    builder = ImmutableSet.builder();
    builder.add(Pair.of("blockGlass", Material.VALUE_Glass));
    builder.add(Pair.of("paneGlass", Material.VALUE_Glass * 6 / 10));
    addKnownOreFluid(TinkerFluids.glass, builder.build());

    TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Blocks.GLASS_PANE), null, TinkerFluids.glass, Material.VALUE_Glass * 6 / 10, 50));
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockClearGlass), null, TinkerFluids.glass, Material.VALUE_Glass, 120));


    // melt entities into a pulp
    TinkerRegistry.registerEntityMelting(EntityIronGolem.class, new FluidStack(TinkerFluids.cast_iron, 18));
    if(TConstruct.instance.tfc) {
      TinkerRegistry.registerEntityMelting(EntitySnowman.class, new FluidStack(FluidsTFC.FRESH_WATER.get(), 100));
    } else {
      TinkerRegistry.registerEntityMelting(EntitySnowman.class, new FluidStack(FluidRegistry.WATER, 100));
    }
    TinkerRegistry.registerEntityMelting(EntityVillager.class, new FluidStack(TinkerFluids.emerald, 6));
    TinkerRegistry.registerEntityMelting(EntityVindicator.class, new FluidStack(TinkerFluids.emerald, 6));
    TinkerRegistry.registerEntityMelting(EntityEvoker.class, new FluidStack(TinkerFluids.emerald, 6));
    TinkerRegistry.registerEntityMelting(EntityIllusionIllager.class, new FluidStack(TinkerFluids.emerald, 6));
  }

  /**
   * Called by Tinkers Integration to register allows, some are conditional on integrations being loaded
   */
  public static void registerAlloys() {
    if(!isSmelteryLoaded()) {
      return;
    }

    // 1 bucket lava + 1 bucket water = 2 ingots = 1 block obsidian
    // 1000 + 1000 = 288
    // 125 + 125 = 36
    if(Config.obsidianAlloy) {
      if(TConstruct.instance.tfc) {
        TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.obsidian, 36),
            new FluidStack(FluidsTFC.FRESH_WATER.get(), 125),
            new FluidStack(FluidRegistry.LAVA, 125));
      } else {
        TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.obsidian, 36),
            new FluidStack(FluidRegistry.WATER, 125),
            new FluidStack(FluidRegistry.LAVA, 125));
      }
    }

    // 1 bucket water + 4 seared ingot + 4 mud bricks = 1 block hardened clay
    // 1000 + 288 + 576 = 576
    // 250 + 72 + 144 = 144
    if(TConstruct.instance.tfc) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.clay, Config.ingotValue),
          new FluidStack(FluidsTFC.FRESH_WATER.get(), 250),
          new FluidStack(TinkerFluids.searedStone, Config.ingotValue / 2),
          new FluidStack(TinkerFluids.dirt, Config.ingotValue));
    } else {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.clay, Config.ingotValue),
          new FluidStack(FluidRegistry.WATER, 250),
          new FluidStack(TinkerFluids.searedStone, Config.ingotValue / 2),
          new FluidStack(TinkerFluids.dirt, Config.ingotValue));
    }

    // 1 iron ingot + 1 purple slime ball + seared stone in molten form = 1 knightslime ingot
    // 144 + 250 + 288 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.knightslime, Config.ingotValue / 2),
                                 new FluidStack(TinkerFluids.cast_iron, Config.ingotValue / 2),
                                 new FluidStack(TinkerFluids.purpleSlime, 125),
                                 new FluidStack(TinkerFluids.searedStone, Config.ingotValue));

    // i iron ingot + 1 blood... unit thingie + 1/3 gem = 1 pigiron
    // 144 + 99 + 222 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.pig_iron, Config.ingotValue),
                                 new FluidStack(TinkerFluids.cast_iron, Config.ingotValue),
                                 new FluidStack(TinkerFluids.blood, 40),
                                 new FluidStack(TinkerFluids.clay, Config.ingotValue / 2));

    // 1 ingot cobalt + 1 ingot ardite = 1 ingot manyullyn!
    // 144 + 144 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.manyullyn, 2),
                                 new FluidStack(TinkerFluids.cobalt, 2),
                                 new FluidStack(TinkerFluids.ardite, 2));

    // 3 ingots copper + 1 ingot tin = 4 ingots bronze
    if(TinkerIntegration.isIntegrated(TinkerFluids.bronze) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.tin)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.bronze, 4),
                                   new FluidStack(TinkerFluids.copper, 3),
                                   new FluidStack(TinkerFluids.tin, 1));
    }

    // 1 ingot gold + 1 ingot silver = 2 ingots electrum
    if(TinkerIntegration.isIntegrated(TinkerFluids.electrum) &&
       TinkerIntegration.isIntegrated(TinkerFluids.gold) &&
       TinkerIntegration.isIntegrated(TinkerFluids.silver)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.electrum, 2),
                                   new FluidStack(TinkerFluids.gold, 1),
                                   new FluidStack(TinkerFluids.silver, 1));
    }

    // 1 ingot copper + 3 ingots aluminium = 4 ingots alubrass
    if(TinkerIntegration.isIntegrated(TinkerFluids.alubrass) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.aluminum)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.alubrass, 4),
                                   new FluidStack(TinkerFluids.copper, 1),
                                   new FluidStack(TinkerFluids.aluminum, 3));
    }

    // 2 ingots copper + 1 ingot zinc = 3 ingots brass
    if(TinkerIntegration.isIntegrated(TinkerFluids.brass) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.zinc)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.brass, 3),
                                   new FluidStack(TinkerFluids.copper, 2),
                                   new FluidStack(TinkerFluids.zinc, 1));
    }
  }

  /**
   * Called by MaterialIntegration's to register tool part recipes
   * @param material
   */
  public static void registerToolpartMeltingCasting(Material material) {
    // melt ALL the toolparts n stuff. Also cast them.
    Fluid fluid = material.getFluid();
    for(IToolPart toolPart : TinkerRegistry.getToolParts()) {
      if(!toolPart.canBeCasted()) {
        continue;
      }
      if(!toolPart.canUseMaterial(material)) {
        continue;
      }
      if(toolPart instanceof MaterialItem) {
        ItemStack stack = toolPart.getItemstackWithMaterial(material);
        ItemStack cast = new ItemStack(TinkerSmeltery.cast);
        Cast.setTagForPart(cast, stack.getItem());

        if(fluid != null) {
          if(Config.meltingToolpart) {
            // melting
            TinkerRegistry.registerMelting(stack, fluid, toolPart.getCost());
          }
          if(Config.castingToolpart) {
            // casting
            TinkerRegistry.registerTableCasting(stack, cast, fluid, toolPart.getCost());
          }
        }
        // register cast creation from the toolparts
        for(FluidStack fs : castCreationFluids) {
          TinkerRegistry.registerTableCasting(new CastingRecipe(cast,
                                                                RecipeMatch.ofNBT(stack),
                                                                fs,
                                                                true, true));
        }

        // clay casts
        if(Config.claycasts) {
          ItemStack clayCast = new ItemStack(TinkerSmeltery.clayCast);
          Cast.setTagForPart(clayCast, stack.getItem());

          if(fluid != null) {
            RecipeMatch rm = RecipeMatch.ofNBT(clayCast);
            FluidStack fs = new FluidStack(fluid, toolPart.getCost());
            TinkerRegistry.registerTableCasting(new CastingRecipe(stack, rm, fs, true, false));
          }
          for(FluidStack fs : clayCreationFluids) {
            TinkerRegistry.registerTableCasting(new CastingRecipe(clayCast,
                                                                  RecipeMatch.ofNBT(stack),
                                                                  fs,
                                                                  true, true));
          }
        }
      }
    }

    // same for shard
    if(castShard != null) {
      ItemStack stack = TinkerRegistry.getShard(material);
      int cost = TinkerRegistry.getShard().getCost();

      if(fluid != null) {
        // melting
        TinkerRegistry.registerMelting(stack, fluid, cost);
        // casting
        TinkerRegistry.registerTableCasting(stack, castShard, fluid, cost);
      }
      // register cast creation from the toolparts
      for(FluidStack fs : castCreationFluids) {
        TinkerRegistry.registerTableCasting(new CastingRecipe(castShard,
                                                              RecipeMatch.ofNBT(stack),
                                                              fs,
                                                              true, true));
      }
    }
  }

  /**
   * Registers melting for all directly supported pre- and suffixes of the ore.
   * E.g. "Iron" -> "ingotIron", "blockIron", "oreIron",
   */
  @SuppressWarnings("unchecked")
  public static void registerOredictMeltingCasting(Fluid fluid, String ore) {
    ImmutableSet.Builder<Pair<String, Integer>> builder = ImmutableSet.builder();
    Pair<String, Integer> nuggetOre = Pair.of("nugget" + ore, Material.VALUE_Nugget);
    Pair<String, Integer> ingotOre = Pair.of("ingot" + ore, Material.VALUE_Ingot);
    Pair<String, Integer> blockOre = Pair.of("block" + ore, Material.VALUE_Block);
    Pair<String, Integer> oreOre = Pair.of("ore" + ore, Material.VALUE_Ore());
    Pair<String, Integer> oreNetherOre = Pair.of("oreNether" + ore, (int) (2 * Material.VALUE_Ingot * Config.oreToIngotRatio));
    Pair<String, Integer> oreDenseOre = Pair.of("denseore" + ore, (int) (3 * Material.VALUE_Ingot * Config.oreToIngotRatio));
    Pair<String, Integer> orePoorOre = Pair.of("orePoor" + ore, (int) (Material.VALUE_Nugget * 3 * Config.oreToIngotRatio));
    Pair<String, Integer> oreNuggetOre = Pair.of("oreNugget" + ore, (int) (Material.VALUE_Nugget * Config.oreToIngotRatio));
    Pair<String, Integer> plateOre = Pair.of("plate" + ore, Material.VALUE_Plate);
    Pair<String, Integer> gearOre = Pair.of("gear" + ore, Material.VALUE_Gear);
    Pair<String, Integer> dustOre = Pair.of("dust" + ore, Material.VALUE_Ingot);

    builder.add(nuggetOre, ingotOre, blockOre, oreOre, oreNetherOre, oreDenseOre, orePoorOre, oreNuggetOre, plateOre, gearOre, dustOre);
    Set<Pair<String, Integer>> knownOres = builder.build();

    // register oredicts
    addKnownOreFluid(fluid, knownOres);

    // register oredict castings!
    // ingot casting
    if(Config.castingIngot) {
      TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(ingotOre.getLeft(),
              RecipeMatch.ofNBT(castIngot),
              fluid,
              Config.ingotValueCasting));
    }

    // nugget casting
    if(Config.castingNugget) {
      TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(nuggetOre.getLeft(),
              RecipeMatch.ofNBT(castNugget),
              fluid,
              Config.nuggetValueCasting));
    }

    // block casting
    if(Config.castingBlock) {
      TinkerRegistry.registerBasinCasting(new PreferenceCastingRecipe(blockOre.getLeft(),
              null, // no cast
              fluid,
              Config.blockValueCasting));
    }

    // plate casting
    if(Config.castingPlate) {
      TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(plateOre.getLeft(),
              RecipeMatch.ofNBT(castPlate),
              fluid,
              Config.plateValueCasting));
    }

    // gear casting
      if(Config.castingGear) {
        TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(gearOre.getLeft(),
                RecipeMatch.ofNBT(castGear),
                fluid,
                Config.gearValueCasting));
      }

    // and also cast creation!
    if(Config.castingCast) {
      for (FluidStack fs : castCreationFluids) {
        TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of(ingotOre.getLeft()), fs, true, true));
        TinkerRegistry.registerTableCasting(new CastingRecipe(castNugget, RecipeMatch.of(nuggetOre.getLeft()), fs, true, true));
        TinkerRegistry.registerTableCasting(new CastingRecipe(castPlate, RecipeMatch.of(plateOre.getLeft()), fs, true, true));
        TinkerRegistry.registerTableCasting(new CastingRecipe(castGear, RecipeMatch.of(gearOre.getLeft()), fs, true, true));
      }
    }
  }

  /**
   * Adds a fluid to the knownOreFluids list, adding recipes for each combination
   * @param fluid      Fluid recipes belong to
   * @param knownOres  Set of pairs of an oredict name to a integer fluid amount
   */
  private static void addKnownOreFluid(Fluid fluid, Set<Pair<String, Integer>> knownOres) {
    if(Config.meltingOreDict) {
      for (Pair<String, Integer> pair : knownOres) {
        TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
      }
    }
    knownOreFluids.put(fluid, knownOres);
  }

  /**
   * take all fluids we registered oredicts for and scan all recipies for oredict-recipies that we can apply this to
   *
   * called in TinkerIntegration
   */
  public static void registerRecipeOredictMelting() {
    if(!isSmelteryLoaded()) {
      return;
    }

    log.info("Started adding oredict melting recipes");
    long start = System.nanoTime();

    // parse the ignore list from the config
    RecipeMatchRegistry oredictMeltingIgnore = new RecipeMatchRegistry();
    for(String ignore : Config.oredictMeltingIgnore) {
      // skip comments and empty lines
      if(ignore.isEmpty() || ignore.startsWith("#")) {
        continue;
      }

      // if it has a colon, assume item stack
      if(ignore.contains(":")) {
        String[] parts = ignore.split(":");
        int meta = OreDictionary.WILDCARD_VALUE;

        // try parsing meta if given
        if(parts.length > 2) {
          try {
            meta = Integer.parseInt(parts[2]);
          } catch(NumberFormatException e) {
            log.error("Invalid oredict melting ignore {}, metadata must be a number", ignore);
            continue;
          }
          if(meta < 0) {
            log.error("Invalid oredict melting ignore {}, metadata must be non-negative", ignore);
            continue;
          }
        }

        // find the item
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if(item == null || item == Items.AIR) {
          log.error("Invalid oredict melting ignore {}, unknown item", ignore);
          continue;
        }

        // add the override
        oredictMeltingIgnore.addItem(new ItemStack(item, 1, meta), 1, 1);
      } else {
        oredictMeltingIgnore.addItem(ignore);
      }
    }

    // we go through all recipes, and check if it only consists of one of our known oredict entries
    recipes:
    for(IRecipe irecipe : CraftingManager.REGISTRY) {
      // empty?
      ItemStack output = irecipe.getRecipeOutput();
      if(output.isEmpty()) {
        continue;
      }

      // blacklisted?
      for(ItemStack blacklistItem : meltingBlacklist) {
        if(OreDictionary.itemMatches(blacklistItem, output, false)) {
          continue recipes;
        }
      }

      // recipe already has a melting recipe?
      if(TinkerRegistry.getMelting(output) != null) {
        continue;
      }

      NonNullList<Ingredient> inputs = irecipe.getIngredients();

      // this map holds how much of which fluid is known of the recipe
      // if an recipe contains an itemstack that can't be mapped to a fluid calculation is aborted
      Map<Fluid, Integer> known = Maps.newHashMap();
      for(Ingredient ingredient : inputs) {
        // can contain empty entries because of shapedrecipe
        if(ingredient.getMatchingStacks().length == 0) {
          continue;
        }

        // process skippable items, such as sticks
        if(Arrays.stream(ingredient.getMatchingStacks()).anyMatch((stack) -> oredictMeltingIgnore.matches(stack).isPresent())) {
          continue;
        }

        // try and find a match from the oredict list
        boolean found = false;
        knownOres:
        for(Map.Entry<Fluid, Set<Pair<String, Integer>>> entry : knownOreFluids.entrySet()) {
          // check if it's a known oredict (all oredict lists are equal if they match the same oredict)
          // OR if it's an itemstack contained in one of our oredicts
          for(Pair<String, Integer> pair : entry.getValue()) {
            for(ItemStack itemStack : OreDictionary.getOres(pair.getLeft(), false)) {
              if(ingredientMatches(ingredient, itemStack)) {
                // matches! Update fluid amount known
                Integer amount = known.get(entry.getKey()); // what we found for the liquid so far
                if(amount == null) {
                  // nothing is what we found so far.
                  amount = 0;
                }
                amount += pair.getRight();
                known.put(entry.getKey(), amount);
                found = true;
                break knownOres;
              }
            }
          }
        }
        // not a recipe we can process, contains an item that can't melt
        if(!found) {
          continue recipes;
        }
      }

      // add a melting recipe for it
      // we only support single-liquid recipes currently :I
      if(known.keySet().size() == 1) {
        Fluid fluid = known.keySet().iterator().next();
        output = output.copy();
        int amount = known.get(fluid) / output.getCount();
        output.setCount(1);
        TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(output, amount), fluid));
        log.trace("Added automatic melting recipe for {} ({} {})", output.toString(), amount, fluid
            .getName());
      }
    }
    // how fast were we?
    log.info("Oredict melting recipes finished in {} ms", (System.nanoTime() - start) / 1000000D);
  }

  /**
   * Ingredients do not handle the passed in item stack having wildcard metadata, so handle using getSubItems
   */
  private static boolean ingredientMatches(Ingredient ingredient, ItemStack stack) {
    if (stack.getMetadata() != OreDictionary.WILDCARD_VALUE) {
      return ingredient.apply(stack);
    }
    NonNullList<ItemStack> stacks = NonNullList.create();
    stack.getItem().getSubItems(CreativeTabs.SEARCH, stacks);
    return stacks.stream().anyMatch(ingredient::apply);
  }
}
