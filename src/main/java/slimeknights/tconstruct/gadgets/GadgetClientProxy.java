package slimeknights.tconstruct.gadgets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.gadgets.block.BlockSlimeChannel;
import slimeknights.tconstruct.gadgets.client.RenderThrowball;
import slimeknights.tconstruct.gadgets.entity.EntityThrowball;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.PropertyStateMapper;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.shared.block.BlockSlime;

import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemBlockMeta;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerItemModel;
import static slimeknights.tconstruct.common.ModelRegisterUtil.registerToolModel;

public class GadgetClientProxy extends ClientProxy {

  @Override
  public void preInit() {
    super.preInit();

    MinecraftForge.EVENT_BUS.register(new GadgetClientEvents());
  }

  @Override
  public void init() {
    Minecraft minecraft = Minecraft.getMinecraft();

    // slime channels
    minecraft.getBlockColors().registerBlockColorHandler(
        (@Nonnull IBlockState state, IBlockAccess access, BlockPos pos, int tintIndex) -> state.getValue(BlockSlimeChannel.TYPE).getColor(),
        TinkerGadgets.slimeChannel);

    ItemColors colors = minecraft.getItemColors();
    colors.registerItemColorHandler(
        (@Nonnull ItemStack stack, int tintIndex) -> BlockSlime.SlimeType.fromMeta(stack.getItemDamage()).getColor(),
        TinkerGadgets.slimeChannel);
    colors.registerItemColorHandler(
        (@Nonnull ItemStack stack, int tintIndex) -> TinkerGadgets.slimeBoots.getColor(stack),
        TinkerGadgets.slimeBoots, TinkerGadgets.slimeSling);


    super.init();
  }

  @Override
  public void registerModels() {
    super.registerModels();

    // separate the sides into separate model files to make the blockstate rotations easier
    ModelLoader.setCustomStateMapper(TinkerGadgets.slimeChannel, new PropertyStateMapper("slime_channel", BlockSlimeChannel.SIDE, BlockSlimeChannel.TYPE));

    // Blocks
    registerItemModel(TinkerGadgets.punji);
    registerItemModel(TinkerGadgets.rack);

    registerItemModel(TinkerGadgets.woodRail);
    registerItemModel(TinkerGadgets.woodRailTrapdoor);

    registerItemModel(TinkerGadgets.slimeChannel); //tinted for variants

    // Items
    registerItemModel(TinkerGadgets.slimeSling);
    registerItemModel(TinkerGadgets.slimeBoots);
    registerItemModel(TinkerGadgets.piggybackPack);
    registerItemModel(TinkerGadgets.stoneStick);

    for(ItemThrowball.ThrowballType type : ItemThrowball.ThrowballType.values()) {
      registerItemModel(TinkerGadgets.throwball, type.ordinal(), type.name().toLowerCase(Locale.US));
    }

    // Entity
    RenderingRegistry.registerEntityRenderingHandler(EntityThrowball.class, RenderThrowball.FACTORY);


    // Mom's Spaghetti
    TinkerGadgets.spaghetti.registerItemModels();
    registerToolModel(TinkerGadgets.momsSpaghetti, Util.getResource("moms_spaghetti" + ToolModelLoader.EXTENSION));
  }

  @Override
  public void postInit() {
    super.postInit();
  }
}
