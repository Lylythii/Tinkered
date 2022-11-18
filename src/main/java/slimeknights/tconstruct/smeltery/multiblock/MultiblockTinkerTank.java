package slimeknights.tconstruct.smeltery.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class MultiblockTinkerTank extends MultiblockTinker {

  public MultiblockTinkerTank(TileTinkerTank tank) {
    // ceiling, floor, and walls
    super(tank, true, true, true);
  }

  @Override
  public boolean isValidBlock(World world, BlockPos pos) {
    // controller always is valid
    if(pos.equals(tile.getPos())) {
      return true;
    }

    // main structure can use anything
    return TinkerSmeltery.validTinkerTankBlocks.contains(world.getBlockState(pos).getBlock()) && isValidSlave(world, pos);
  }

  @Override
  public boolean isFrameBlock(World world, BlockPos pos, EnumFrameType type) {
    // controller always is valid
    if(pos.equals(tile.getPos())) {
      return true;
    }

    if(!isValidSlave(world, pos)) {
      return false;
    }

    // the side frames are fine to be anything like normal blocks
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    if(type == EnumFrameType.WALL) {
      return TinkerSmeltery.validTinkerTankBlocks.contains(block);
    }

    // the bottom and top frames have to be seared blocks or drains, for structure
    return block == TinkerSmeltery.searedBlock || block == TinkerSmeltery.smelteryIO;
  }

  @Override
  public boolean isFloorBlock(World world, BlockPos pos) {
    // only bricks for the floor
    return TinkerSmeltery.validTinkerTankFloorBlocks.contains(world.getBlockState(pos).getBlock()) && isValidSlave(world, pos);
  }

}
