package slimeknights.tconstruct.gadgets;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.logging.log4j.Logger;

import net.dries007.tfc.objects.fluids.FluidsTFC;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.gadgets.block.BlockPunji;
import slimeknights.tconstruct.gadgets.block.BlockRack;
import slimeknights.tconstruct.gadgets.block.BlockSlimeChannel;
import slimeknights.tconstruct.gadgets.block.BlockWoodRail;
import slimeknights.tconstruct.gadgets.block.BlockWoodRailDropper;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.gadgets.item.ItemBlockRack;
import slimeknights.tconstruct.gadgets.item.ItemMomsSpaghetti;
import slimeknights.tconstruct.gadgets.item.ItemPiggybackPack;
import slimeknights.tconstruct.gadgets.item.ItemSlimeBoots;
import slimeknights.tconstruct.gadgets.item.ItemSlimeSling;
import slimeknights.tconstruct.gadgets.item.ItemSpaghetti;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.gadgets.modifiers.ModSpaghettiMeat;
import slimeknights.tconstruct.gadgets.modifiers.ModSpaghettiSauce;
import slimeknights.tconstruct.gadgets.tileentity.TileDryingRack;
import slimeknights.tconstruct.gadgets.tileentity.TileItemRack;
import slimeknights.tconstruct.gadgets.tileentity.TileSlimeChannel;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSlime.SlimeType;

@Pulse(id = TinkerGadgets.PulseId, description = "All the fun toys")
public class TinkerGadgets extends TinkerPulse {

  public static final String PulseId = "TinkerGadgets";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.gadgets.GadgetClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static Block punji;
  public static BlockRack rack;

  public static Block woodRail;
  public static Block woodRailTrapdoor;

  public static BlockSlimeChannel slimeChannel;

  public static ItemSlimeSling slimeSling;
  public static ItemSlimeBoots slimeBoots;
  public static ItemPiggybackPack piggybackPack;
  public static ItemThrowball throwball;
  public static Item stoneStick;

  public static ItemMetaDynamic spaghetti;
  public static ItemMomsSpaghetti momsSpaghetti;
  public static Modifier modSpaghettiSauce;
  public static Modifier modSpaghettiMeat;

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    punji = registerBlock(registry, new BlockPunji(), "punji");
    rack = registerBlock(registry, new BlockRack(), "rack");

    woodRail = registerBlock(registry, new BlockWoodRail(), "wood_rail");
    woodRailTrapdoor = registerBlock(registry, new BlockWoodRailDropper(), "wood_rail_trapdoor");

    // slime channels
    slimeChannel = registerBlock(registry, new BlockSlimeChannel(), "slime_channel");

    registerTE(TileItemRack.class, "item_rack");
    registerTE(TileDryingRack.class, "drying_rack");
    registerTE(TileSlimeChannel.class, "slime_channel");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    punji = registerItemBlock(registry, punji);
    rack = registerItemBlock(registry, new ItemBlockRack(rack));

    woodRail = registerItemBlock(registry, woodRail);
    woodRailTrapdoor = registerItemBlock(registry, woodRailTrapdoor);

    // slime channels
    slimeChannel = registerEnumItemBlock(registry, slimeChannel);

    slimeSling = registerItem(registry, new ItemSlimeSling(), "slimesling");
    slimeBoots = registerItem(registry, new ItemSlimeBoots(), "slime_boots");
    piggybackPack = registerItem(registry, new ItemPiggybackPack(), "piggybackpack");
    throwball = registerItem(registry, new ItemThrowball(), "throwball");
    stoneStick = registerItem(registry, new Item(), "stone_stick");
    stoneStick.setFull3D().setCreativeTab(TinkerRegistry.tabGadgets);

    spaghetti = registerItem(registry, new ItemSpaghetti(), "spaghetti");
    momsSpaghetti = registerItem(registry, new ItemMomsSpaghetti(), "moms_spaghetti");

    ItemStack hardSpaghetti = spaghetti.addMeta(0, "hard");
    ItemStack wetSpaghetti = spaghetti.addMeta(1, "soggy");
    ItemStack coldSpaghetti = spaghetti.addMeta(2, "cold");

    modSpaghettiSauce = new ModSpaghettiSauce();

    modSpaghettiMeat = new ModSpaghettiMeat();

    MinecraftForge.EVENT_BUS.register(slimeBoots);
  }

  @SubscribeEvent
  public void registerEntities(Register<EntityEntry> event) {
    EntityRegistry.registerModEntity(Util.getResource("throwball"), EntityThrowball.class, "Throwball", EntityIDs.THROWBALL, TConstruct.instance, 64, 10, true);
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
    registerSmelting();

    proxy.init();
  }

  private void registerSmelting() {
    // slime channels
    for(SlimeType type : SlimeType.values()) {
      GameRegistry.addSmelting(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, type.getMeta()),
                               new ItemStack(slimeChannel, 3, type.getMeta()), 0.15f);
    }

    // prevents items from despawning in slime channels
    MinecraftForge.EVENT_BUS.register(BlockSlimeChannel.EventHandler.instance);
    MinecraftForge.EVENT_BUS.register(new GadgetEvents());

    proxy.postInit();

    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(slimeSling));
  }

  private void registerDrying() {
    // Jerky
    int time = 20 * 60 * 5;
    TinkerRegistry.registerDryingRecipe(Items.ROTTEN_FLESH, TinkerCommons.jerkyMonster, time);
    TinkerRegistry.registerDryingRecipe(Items.SPIDER_EYE, TinkerCommons.jerkySpider, time);
    TinkerRegistry.registerDryingRecipe(Items.BEEF, TinkerCommons.jerkyBeef, time);
    TinkerRegistry.registerDryingRecipe(Items.CHICKEN, TinkerCommons.jerkyPoultry, time);
    TinkerRegistry.registerDryingRecipe(Items.PORKCHOP, TinkerCommons.jerkyPork, time);
    TinkerRegistry.registerDryingRecipe(Items.MUTTON, TinkerCommons.jerkyMutton, time);
    TinkerRegistry.registerDryingRecipe(Items.RABBIT, TinkerCommons.jerkyRabbit, time);

    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 0), TinkerCommons.jerkyFish, time);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 1), TinkerCommons.jerkySalmon, time);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 2), TinkerCommons.jerkyClownfish, time);
    TinkerRegistry.registerDryingRecipe(new ItemStack(Items.FISH, 1, 3), TinkerCommons.jerkyPufferfish, time);

    TinkerRegistry.registerDryingRecipe(Items.SLIME_BALL, TinkerCommons.slimedropGreen, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallBlue, TinkerCommons.slimedropBlue, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallPurple, TinkerCommons.slimedropPurple, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallBlood, TinkerCommons.slimedropBlood, time);
    TinkerRegistry.registerDryingRecipe(TinkerCommons.matSlimeBallMagma, TinkerCommons.slimedropMagma, time);

    // leather
    if(Config.leatherDryingRecipe) {
      ItemStack leather = new ItemStack(Items.LEATHER);
      time = (int) (20 * 60 * 8.5);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_BEEF, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_CHICKEN, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_FISH, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_MUTTON, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_PORKCHOP, leather, time);
      TinkerRegistry.registerDryingRecipe(Items.COOKED_RABBIT, leather, time);
    }

    // Wet sponge to dry sponge
    TinkerRegistry.registerDryingRecipe(new ItemStack(Blocks.SPONGE, 1, 1), new ItemStack(Blocks.SPONGE, 1, 0), 20 * 60 * 2);

    // Sapling to dead bush
    TinkerRegistry.registerDryingRecipe("treeSapling", new ItemStack(Blocks.DEADBUSH), 20 * 60 * 6);
  }
}
