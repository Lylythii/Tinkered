package slimeknights.tconstruct.tools;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.tools.traits.TraitEnderference;
import slimeknights.tconstruct.tools.traits.TraitInsatiable;
import slimeknights.tconstruct.tools.traits.TraitMagnetic;
import slimeknights.tconstruct.tools.traits.TraitMomentum;
import slimeknights.tconstruct.tools.traits.TraitSharp;
import slimeknights.tconstruct.tools.traits.TraitSplintering;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;

import static slimeknights.tconstruct.library.materials.MaterialTypes.HEAD;
import static slimeknights.tconstruct.library.materials.MaterialTypes.PROJECTILE;
import static slimeknights.tconstruct.library.materials.MaterialTypes.SHAFT;
import static slimeknights.tconstruct.library.utils.HarvestLevels.COBALT;
import static slimeknights.tconstruct.library.utils.HarvestLevels.DIAMOND;
import static slimeknights.tconstruct.library.utils.HarvestLevels.IRON;
import static slimeknights.tconstruct.library.utils.HarvestLevels.OBSIDIAN;
import static slimeknights.tconstruct.library.utils.HarvestLevels.STONE;
import static slimeknights.tconstruct.tools.TinkerTraits.alien;
import static slimeknights.tconstruct.tools.TinkerTraits.aquadynamic;
import static slimeknights.tconstruct.tools.TinkerTraits.aridiculous;
import static slimeknights.tconstruct.tools.TinkerTraits.autosmelt;
import static slimeknights.tconstruct.tools.TinkerTraits.breakable;
import static slimeknights.tconstruct.tools.TinkerTraits.cheap;
import static slimeknights.tconstruct.tools.TinkerTraits.cheapskate;
import static slimeknights.tconstruct.tools.TinkerTraits.coldblooded;
import static slimeknights.tconstruct.tools.TinkerTraits.crude;
import static slimeknights.tconstruct.tools.TinkerTraits.crude2;
import static slimeknights.tconstruct.tools.TinkerTraits.crumbling;
import static slimeknights.tconstruct.tools.TinkerTraits.dense;
import static slimeknights.tconstruct.tools.TinkerTraits.duritos;
import static slimeknights.tconstruct.tools.TinkerTraits.ecological;
import static slimeknights.tconstruct.tools.TinkerTraits.enderference;
import static slimeknights.tconstruct.tools.TinkerTraits.endspeed;
import static slimeknights.tconstruct.tools.TinkerTraits.established;
import static slimeknights.tconstruct.tools.TinkerTraits.flammable;
import static slimeknights.tconstruct.tools.TinkerTraits.fractured;
import static slimeknights.tconstruct.tools.TinkerTraits.freezing;
import static slimeknights.tconstruct.tools.TinkerTraits.heavy;
import static slimeknights.tconstruct.tools.TinkerTraits.hellish;
import static slimeknights.tconstruct.tools.TinkerTraits.holy;
import static slimeknights.tconstruct.tools.TinkerTraits.hovering;
import static slimeknights.tconstruct.tools.TinkerTraits.insatiable;
import static slimeknights.tconstruct.tools.TinkerTraits.jagged;
import static slimeknights.tconstruct.tools.TinkerTraits.lightweight;
import static slimeknights.tconstruct.tools.TinkerTraits.magnetic;
import static slimeknights.tconstruct.tools.TinkerTraits.magnetic2;
import static slimeknights.tconstruct.tools.TinkerTraits.momentum;
import static slimeknights.tconstruct.tools.TinkerTraits.petramor;
import static slimeknights.tconstruct.tools.TinkerTraits.poisonous;
import static slimeknights.tconstruct.tools.TinkerTraits.prickly;
import static slimeknights.tconstruct.tools.TinkerTraits.sharp;
import static slimeknights.tconstruct.tools.TinkerTraits.shocking;
import static slimeknights.tconstruct.tools.TinkerTraits.slimeyBlue;
import static slimeknights.tconstruct.tools.TinkerTraits.slimeyGreen;
import static slimeknights.tconstruct.tools.TinkerTraits.spiky;
import static slimeknights.tconstruct.tools.TinkerTraits.splintering;
import static slimeknights.tconstruct.tools.TinkerTraits.splitting;
import static slimeknights.tconstruct.tools.TinkerTraits.squeaky;
import static slimeknights.tconstruct.tools.TinkerTraits.stiff;
import static slimeknights.tconstruct.tools.TinkerTraits.stonebound;
import static slimeknights.tconstruct.tools.TinkerTraits.superheat;
import static slimeknights.tconstruct.tools.TinkerTraits.tasty;
import static slimeknights.tconstruct.tools.TinkerTraits.unnatural;
import static slimeknights.tconstruct.tools.TinkerTraits.writable;
import static slimeknights.tconstruct.tools.TinkerTraits.writable2;

/**
 * All the tool materials tcon supports.
 */
@Pulse(id = TinkerMaterials.PulseId, description = "All the tool materials added by TConstruct", pulsesRequired = TinkerTools.PulseId, forced = true)
public final class TinkerMaterials {

  static final String PulseId = "TinkerMaterials";
  static final Logger log = Util.getLogger(PulseId);

  public static final List<Material> materials = Lists.newArrayList();
  // not all listed materials are available by default. They enable when the needed material is present. See TinkerIntegration

  // natural resources/blocks
  public static final Material wood					= mat("wood", 0x8e661b);
  public static final Material stone      			= mat("stone", 0x999999);
  public static final Material flint      			= mat("flint", 0x696969);
  public static final Material bone       			= mat("bone", 0xede6bf);
  public static final Material obsidian   			= mat("obsidian", 0x601cc4);

  // Slime
  public static final Material knightslime			= mat("knightslime", 0xf18ff0);
  public static final Material slime      			= mat("slime", 0x82c873);
  public static final Material blueslime  			= mat("blueslime", 0x74c8c7);
  public static final Material magmaslime 			= mat("magmaslime", 0xff960d);

  // Metals
  public static final Material cast_iron			= mat("cast_iron", 0xFF40494D);
  public static final Material pig_iron				= mat("pig_iron", 0xFF6A595C);
  public static final Material wrought_iron			= mat("wrought_iron", 0xFF989897);

  // Nether Materials
  public static final Material ardite				= mat("ardite", 0xd14210);
  public static final Material cobalt				= mat("cobalt", 0x2882d4);
  public static final Material manyullyn			= mat("manyullyn", 0xa15cf8);

  // mod integration
  public static final Material copper     			= mat("copper", 0xFFB64027);
  public static final Material bronze     			= mat("bronze", 0xFF7C5E33);
  public static final Material bismuth_bronze		= mat("bismuth_bronze", 0xFF418E4F);
  public static final Material black_bronze			= mat("black_bronze", 0xFF3B2636);
  public static final Material lead       			= mat("lead", 0xFF40494D);
  public static final Material silver     			= mat("silver", 0xFF949495);
  public static final Material electrum   			= mat("electrum", 0xE8DB49);
  public static final Material steel      			= mat("steel", 0xFF5F5F5F);

  // specul
  public static final Material xu;

  // bowstring materials
  public static final Material string    			= mat("string", 0xeeeeee);
  public static final Material vine      			= mat("vine", 0x40a10f);
  public static final Material slimevine_blue		= mat("slimevine_blue", 0x74c8c7);
  //public static final Material slimevine_orange	= mat("slimevine_orange", 0xff960d);
  public static final Material slimevine_purple		= mat("slimevine_purple", 0xc873c8);

  // additional arrow shaft
  public static final Material blaze     			= mat("blaze", 0xffc100);
  public static final Material reed      			= mat("reed", 0xaadb74);
  public static final Material ice       			= mat("ice", 0x97d7e0);
  public static final Material endrod    			= mat("endrod", 0xe8ffd6);

  // fletching
  public static final Material feather   			= mat("feather", 0xeeeeee);
  public static final Material leaf      			= mat("leaf", 0x1d730c);
  public static final Material slimeleaf_blue   	= mat("slimeleaf_blue", 0x74c8c7);
  public static final Material slimeleaf_orange 	= mat("slimeleaf_orange", 0xff960d);
  public static final Material slimeleaf_purple 	= mat("slimeleaf_purple", 0xc873c8);

  private static Material mat(String name, int color) {
    // make materials hidden by default, integration will make them visible if integrated
    Material mat = new Material(name, color, true);
    materials.add(mat);
    return mat;
  }

  static {
    xu = new Material("unstable", TextFormatting.WHITE);
  }

  @SubscribeEvent
  public void registerPotions(Register<Potion> event) {
    IForgeRegistry<Potion> registry = event.getRegistry();

    registry.registerAll(TraitEnderference.Enderference,
                         TraitInsatiable.Insatiable,
                         TraitMagnetic.Magnetic,
                         TraitMomentum.Momentum,
                         TraitSharp.DOT,
                         TraitSplintering.Splinter);
  }

  @Subscribe
  public void setupMaterialStats(FMLPreInitializationEvent event) {
    // stats need to be present before model loading/texture generation so we don't generate unneeded parts
    registerToolMaterialStats();
    registerBowMaterialStats();
    registerProjectileMaterialStats();
  }

  @Subscribe
  public void setupMaterials(FMLInitializationEvent event) {
    // natural resources/blocks
    wood.setCraftable(true);
    wood.addItem("stickWood", 1, Material.VALUE_Shard);
    wood.addItem("plankWood", 1, Material.VALUE_Ingot);
    wood.addItem("logWood", 1, Material.VALUE_Ingot * 4);
    wood.addTrait(ecological);

    stone.setCraftable(true);
    stone.addItemIngot("cobblestone");
    stone.addItemIngot("stone");
    stone.setRepresentativeItem(new ItemStack(Blocks.COBBLESTONE));
    stone.addTrait(cheapskate, HEAD);
    stone.addTrait(cheap);

    flint.setCraftable(true);
    flint.addItem(Items.FLINT, 1, Material.VALUE_Ingot);
    flint.setRepresentativeItem(new ItemStack(Items.FLINT));
    flint.addTrait(crude2, HEAD);
    flint.addTrait(crude);

    obsidian.setFluid(TinkerFluids.obsidian);
    obsidian.setCraftable(true);
    obsidian.setCastable(true);
    obsidian.addItemIngot("obsidian");
    obsidian.setRepresentativeItem(new ItemStack(Blocks.OBSIDIAN));
    obsidian.addTrait(duritos);

    // item/special resources
    bone.setCraftable(true);
    bone.addItemIngot("bone");
    bone.addItem(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), 1, Material.VALUE_Fragment); // bonemeal
    bone.setRepresentativeItem(Items.BONE);
    bone.addTrait(splintering, HEAD);
    bone.addTrait(splitting, SHAFT);
    bone.addTrait(fractured);

    slime.setCraftable(true);
    slime.addItemIngot("slimecrystalGreen");
    slime.addTrait(slimeyGreen);

    blueslime.setCraftable(true);
    blueslime.addItemIngot("slimecrystalBlue");
    blueslime.addTrait(slimeyBlue);

    knightslime.setCraftable(true);
    knightslime.addCommonItems("Knightslime");
    knightslime.addTrait(crumbling, HEAD);
    knightslime.addTrait(unnatural);

    magmaslime.setCraftable(true);
    magmaslime.addItemIngot("slimecrystalMagma");
    magmaslime.setRepresentativeItem(TinkerCommons.matSlimeCrystalMagma);
    magmaslime.addTrait(superheat, HEAD);
    magmaslime.addTrait(flammable);

    // Metals
    cast_iron.addCommonItems("CastIron");
    cast_iron.addTrait(magnetic2, HEAD);
    cast_iron.addTrait(magnetic);

    pig_iron.addCommonItems("PigIron");
    pig_iron.addTrait(magnetic, HEAD);
    pig_iron.addTrait(magnetic);

    wrought_iron.addCommonItems("WroughtIron");
    wrought_iron.addTrait(magnetic2, HEAD);
    wrought_iron.addTrait(magnetic);

    cobalt.addCommonItems("Cobalt");
    cobalt.addTrait(momentum, HEAD);
    cobalt.addTrait(lightweight);

    ardite.addCommonItems("Ardite");
    ardite.addTrait(stonebound, HEAD);
    ardite.addTrait(petramor);

    manyullyn.addCommonItems("Manyullyn");
    manyullyn.addTrait(insatiable, HEAD);
    manyullyn.addTrait(coldblooded);

    // mod integration
    copper.addCommonItems("Copper");
    copper.addTrait(established);

    bronze.addCommonItems("Bronze");
    bronze.addTrait(dense);

    bismuth_bronze.addCommonItems("BismuthBronze");
    bismuth_bronze.addTrait(dense);

    black_bronze.addCommonItems("BlackBronze");
    black_bronze.addTrait(dense);

    lead.addCommonItems("Lead");
    lead.addTrait(poisonous);
    lead.addTrait(heavy);

    silver.addCommonItems("Silver");
    silver.addTrait(holy);

    electrum.addCommonItems("Electrum");
    electrum.addTrait(shocking);

    steel.addCommonItems("Steel");
    steel.addTrait(sharp, HEAD);
    steel.addTrait(stiff);

    // bowstring
    string.addItemIngot("string");
    string.setRepresentativeItem(Items.STRING);
    vine.addItemIngot("vine");
    vine.setRepresentativeItem(Blocks.VINE);
    safeAdd(slimevine_blue, new ItemStack(TinkerWorld.slimeVineBlue1), Material.VALUE_Ingot, true);
    safeAdd(slimevine_blue, new ItemStack(TinkerWorld.slimeVineBlue2), Material.VALUE_Ingot, false);
    safeAdd(slimevine_blue, new ItemStack(TinkerWorld.slimeVineBlue3), Material.VALUE_Ingot, false);
    safeAdd(slimevine_purple, new ItemStack(TinkerWorld.slimeVinePurple1), Material.VALUE_Ingot, true);
    safeAdd(slimevine_purple, new ItemStack(TinkerWorld.slimeVinePurple2), Material.VALUE_Ingot, false);
    safeAdd(slimevine_purple, new ItemStack(TinkerWorld.slimeVinePurple3), Material.VALUE_Ingot, false);

    // arrow only materials
    blaze.addItem(Items.BLAZE_ROD, 1, Material.VALUE_Ingot);
    blaze.setRepresentativeItem(Items.BLAZE_ROD);
    blaze.addTrait(hovering);

    reed.addItem(Items.REEDS, 1, Material.VALUE_Ingot);
    reed.setRepresentativeItem(Items.REEDS);
    reed.addTrait(breakable);

    ice.addItem(Blocks.PACKED_ICE, Material.VALUE_Ingot);
    ice.setRepresentativeItem(Blocks.PACKED_ICE);
    ice.addTrait(freezing);

    endrod.addItem(Blocks.END_ROD, Material.VALUE_Ingot);
    endrod.setRepresentativeItem(Blocks.END_ROD);
    endrod.addTrait(endspeed);

    feather.addItemIngot("feather");
    feather.setRepresentativeItem(Items.FEATHER);
    leaf.addItem("treeLeaves", 1, Material.VALUE_Shard);
    leaf.setRepresentativeItem(Blocks.LEAVES);
    safeAdd(slimeleaf_blue, new ItemStack(TinkerWorld.slimeLeaves, 1, BlockSlimeGrass.FoliageType.BLUE.getMeta()), Material.VALUE_Shard, true);
    safeAdd(slimeleaf_orange, new ItemStack(TinkerWorld.slimeLeaves, 1, BlockSlimeGrass.FoliageType.ORANGE.getMeta()), Material.VALUE_Shard, true);
    safeAdd(slimeleaf_purple, new ItemStack(TinkerWorld.slimeLeaves, 1, BlockSlimeGrass.FoliageType.PURPLE.getMeta()), Material.VALUE_Shard, true);
  }

  private void safeAdd(Material material, ItemStack item, int value) {
    this.safeAdd(material, item, value, false);
  }

  private void safeAddOredicted(Material material, String oredict, ItemStack representative) {
    material.addItem(oredict, 1, Material.VALUE_Ingot);
    material.setRepresentativeItem(representative);
  }

  private void safeAdd(Material material, ItemStack itemStack, int value, boolean representative) {
    if(!itemStack.isEmpty()) {
      material.addItem(itemStack, 1, value);
      if(representative) {
        material.setRepresentativeItem(itemStack);
      }
    }
  }

  public void registerToolMaterialStats() {
    // Stats:                                                   Durability, speed, attack, handle, extra, harvestlevel
    // natural resources/blocks
    TinkerRegistry.addMaterialStats(wood,
                                    new HeadMaterialStats(35, 2.00f, 2.00f, STONE),
                                    new HandleMaterialStats(1.00f, 25),
                                    new ExtraMaterialStats(15));

    TinkerRegistry.addMaterialStats(stone,
                                    new HeadMaterialStats(120, 4.00f, 3.00f, IRON),
                                    new HandleMaterialStats(0.50f, -50),
                                    new ExtraMaterialStats(20));
    TinkerRegistry.addMaterialStats(flint,
                                    new HeadMaterialStats(150, 5.00f, 2.90f, IRON),
                                    new HandleMaterialStats(0.60f, -60),
                                    new ExtraMaterialStats(40));
    TinkerRegistry.addMaterialStats(bone,
                                    new HeadMaterialStats(200, 5.09f, 2.50f, IRON),
                                    new HandleMaterialStats(1.10f, 50),
                                    new ExtraMaterialStats(65));
    TinkerRegistry.addMaterialStats(obsidian,
                                    new HeadMaterialStats(139, 7.07f, 4.20f, COBALT),
                                    new HandleMaterialStats(0.90f, -100),
                                    new ExtraMaterialStats(90));

    // Slime
    TinkerRegistry.addMaterialStats(slime,
                                    new HeadMaterialStats(1000, 4.24f, 1.80f, STONE),
                                    new HandleMaterialStats(0.70f, 0),
                                    new ExtraMaterialStats(350));
    TinkerRegistry.addMaterialStats(blueslime,
                                    new HeadMaterialStats(780, 4.03f, 1.80f, STONE),
                                    new HandleMaterialStats(1.30f, -50),
                                    new ExtraMaterialStats(200));
    TinkerRegistry.addMaterialStats(knightslime,
                                    new HeadMaterialStats(850, 5.8f, 5.10f, OBSIDIAN),
                                    new HandleMaterialStats(0.50f, 500),
                                    new ExtraMaterialStats(125));
    TinkerRegistry.addMaterialStats(magmaslime,
                                    new HeadMaterialStats(600, 2.1f, 7.00f, STONE),
                                    new HandleMaterialStats(0.85f, -200),
                                    new ExtraMaterialStats(150));

    // Nether
    TinkerRegistry.addMaterialStats(cobalt,
                                    new HeadMaterialStats(780, 12.00f, 4.10f, COBALT),
                                    new HandleMaterialStats(0.90f, 100),
                                    new ExtraMaterialStats(300));
    TinkerRegistry.addMaterialStats(ardite,
                                    new HeadMaterialStats(990, 3.50f, 3.60f, COBALT),
                                    new HandleMaterialStats(1.40f, -200),
                                    new ExtraMaterialStats(450));
    TinkerRegistry.addMaterialStats(manyullyn,
                                    new HeadMaterialStats(820, 7.02f, 8.72f, COBALT),
                                    new HandleMaterialStats(0.50f, 250),
                                    new ExtraMaterialStats(50));

    // Metals
    TinkerRegistry.addMaterialStats(cast_iron,
                                    new HeadMaterialStats(204, 6.00f, 4.00f, DIAMOND),
                                    new HandleMaterialStats(0.85f, 60),
                                    new ExtraMaterialStats(50));
    TinkerRegistry.addMaterialStats(pig_iron,
                                    new HeadMaterialStats(380, 6.20f, 4.50f, DIAMOND),
                                    new HandleMaterialStats(1.20f, 0),
                                    new ExtraMaterialStats(170));
    TinkerRegistry.addMaterialStats(wrought_iron,
                                    new HeadMaterialStats(380, 6.20f, 5.00f, DIAMOND),
                                    new HandleMaterialStats(1.85f, 0),
                                    new ExtraMaterialStats(200));

    // Mod Integration
    TinkerRegistry.addMaterialStats(copper,
                                    new HeadMaterialStats(210, 5.30f, 3.00f, IRON),
                                    new HandleMaterialStats(1.05f, 30),
                                    new ExtraMaterialStats(100));

    TinkerRegistry.addMaterialStats(bronze,
                                    new HeadMaterialStats(430, 6.80f, 3.50f, DIAMOND),
                                    new HandleMaterialStats(1.10f, 70),
                                    new ExtraMaterialStats(80));

    TinkerRegistry.addMaterialStats(bismuth_bronze,
                                    new HeadMaterialStats(430, 6.80f, 3.50f, DIAMOND),
                                    new HandleMaterialStats(1.10f, 70),
                                    new ExtraMaterialStats(80));

    TinkerRegistry.addMaterialStats(black_bronze,
                                    new HeadMaterialStats(430, 6.80f, 3.50f, DIAMOND),
                                    new HandleMaterialStats(1.10f, 70),
                                    new ExtraMaterialStats(80));



    TinkerRegistry.addMaterialStats(lead,
                                    new HeadMaterialStats(434, 5.25f, 3.50f, IRON),
                                    new HandleMaterialStats(0.70f, -50),
                                    new ExtraMaterialStats(100));

    TinkerRegistry.addMaterialStats(silver,
                                    new HeadMaterialStats(250, 5.00f, 5.00f, IRON),
                                    new HandleMaterialStats(0.95f, 50),
                                    new ExtraMaterialStats(150));

    TinkerRegistry.addMaterialStats(electrum,
                                    new HeadMaterialStats(50, 12.00f, 3.00f, IRON),
                                    new HandleMaterialStats(1.10f, -25),
                                    new ExtraMaterialStats(250));

    TinkerRegistry.addMaterialStats(steel,
                                    new HeadMaterialStats(540, 7.00f, 6.00f, OBSIDIAN),
                                    new HandleMaterialStats(0.9f, 150),
                                    new ExtraMaterialStats(25));

    //TinkerRegistry.addMaterialStats(xu,         new ToolMaterialStats(97, 1.00f, 1.00f, 0.10f, 0.20f, DIAMOND));
  }

  public void registerBowMaterialStats() {
    BowMaterialStats whyWouldYouMakeABowOutOfThis = new BowMaterialStats(0.2f, 0.4f, -1f);

    TinkerRegistry.addMaterialStats(wood, new BowMaterialStats(1f, 1f, 0));
    TinkerRegistry.addMaterialStats(stone, whyWouldYouMakeABowOutOfThis);
    TinkerRegistry.addMaterialStats(flint, whyWouldYouMakeABowOutOfThis);
    TinkerRegistry.addMaterialStats(bone, new BowMaterialStats(0.95f, 1.15f, 0));
    TinkerRegistry.addMaterialStats(obsidian, whyWouldYouMakeABowOutOfThis);


    // Slime
    TinkerRegistry.addMaterialStats(slime, new BowMaterialStats(0.85f, 1.3f, 0));
    TinkerRegistry.addMaterialStats(blueslime, new BowMaterialStats(1.05f, 1f, 0));
    TinkerRegistry.addMaterialStats(knightslime, new BowMaterialStats(0.4f, 2f, 2f));
    TinkerRegistry.addMaterialStats(magmaslime, new BowMaterialStats(1.1f, 1.05f, 1f));

    // Nether
    TinkerRegistry.addMaterialStats(cobalt, new BowMaterialStats(0.75f, 1.3f, 3f));
    TinkerRegistry.addMaterialStats(ardite, new BowMaterialStats(0.45f, 0.8f, 1f));
    TinkerRegistry.addMaterialStats(manyullyn, new BowMaterialStats(0.65f, 1.2f, 4f));

    // Metals
    TinkerRegistry.addMaterialStats(cast_iron, new BowMaterialStats(0.5f, 1.5f, 7f));
    TinkerRegistry.addMaterialStats(pig_iron, new BowMaterialStats(0.6f, 1.4f, 7f));
    TinkerRegistry.addMaterialStats(wrought_iron, new BowMaterialStats(0.6f, 2f, 8f));

    // Mod Integration
    TinkerRegistry.addMaterialStats(copper, new BowMaterialStats(0.6f, 1.45f, 5f));
    TinkerRegistry.addMaterialStats(bronze, new BowMaterialStats(0.55f, 1.5f, 6f));
    TinkerRegistry.addMaterialStats(bismuth_bronze, new BowMaterialStats(0.55f, 1.5f, 6f));
    TinkerRegistry.addMaterialStats(black_bronze, new BowMaterialStats(0.55f, 1.5f, 6f));
    TinkerRegistry.addMaterialStats(lead, new BowMaterialStats(0.4f, 1.3f, 3f));
    TinkerRegistry.addMaterialStats(silver, new BowMaterialStats(1.2f, 0.8f, 2f));
    TinkerRegistry.addMaterialStats(electrum, new BowMaterialStats(1.5f, 1f, 4f));
    TinkerRegistry.addMaterialStats(steel, new BowMaterialStats(0.4f, 2f, 9f));

    // Bowstring materials
    BowStringMaterialStats bowstring = new BowStringMaterialStats(1f);
    TinkerRegistry.addMaterialStats(string, bowstring);
    TinkerRegistry.addMaterialStats(vine, bowstring);
    TinkerRegistry.addMaterialStats(slimevine_blue, bowstring);
    TinkerRegistry.addMaterialStats(slimevine_purple, bowstring);
  }

  public void registerProjectileMaterialStats() {
    // shaft
    TinkerRegistry.addMaterialStats(wood, new ArrowShaftMaterialStats(1f, 0));
    TinkerRegistry.addMaterialStats(bone, new ArrowShaftMaterialStats(0.9f, 5));
    TinkerRegistry.addMaterialStats(blaze, new ArrowShaftMaterialStats(0.8f, 3));
    TinkerRegistry.addMaterialStats(reed, new ArrowShaftMaterialStats(1.5f, 20));
    TinkerRegistry.addMaterialStats(ice, new ArrowShaftMaterialStats(0.95f, 0));
    TinkerRegistry.addMaterialStats(endrod, new ArrowShaftMaterialStats(0.7f, 1));

    // fletching
    TinkerRegistry.addMaterialStats(feather, new FletchingMaterialStats(1.0f, 1f));
    TinkerRegistry.addMaterialStats(leaf, new FletchingMaterialStats(0.5f, 1.5f));
    FletchingMaterialStats slimeLeafStats = new FletchingMaterialStats(0.8f, 1.25f);
    TinkerRegistry.addMaterialStats(slimeleaf_purple, slimeLeafStats);
    TinkerRegistry.addMaterialStats(slimeleaf_blue, slimeLeafStats);
    TinkerRegistry.addMaterialStats(slimeleaf_orange, slimeLeafStats);
  }


	//Something is broken here
	/*
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    if(TinkerTools.shard == null) {
      return;
    }

    // each material without a shard set gets the default one set
    for(Material material : TinkerRegistry.getAllMaterials()) {
      ItemStack shard = TinkerTools.shard.getItemstackWithMaterial(material);

      material.addRecipeMatch(new RecipeMatch.ItemCombination(Material.VALUE_Shard, shard));
      if(material.getShard() != null) {
        material.setShard(shard);
      }
    }
  }
	*/
}
