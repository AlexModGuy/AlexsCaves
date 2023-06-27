package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class DinosaurEggBlock extends Block {
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;

    private final VoxelShape voxelShape;

    private final RegistryObject<EntityType> births;

    public DinosaurEggBlock(Properties properties, RegistryObject births, VoxelShape voxelShape) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HATCH, Integer.valueOf(0)));
        this.births = births;
        this.voxelShape = voxelShape;
    }

    public DinosaurEggBlock(Properties properties, RegistryObject births, int widthPx, int heightPx) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HATCH, Integer.valueOf(0)));
        this.births = births;
        int px = (16 - widthPx) / 2;
        this.voxelShape = Block.box(px, 0, px, 16 - px, heightPx, 16 - px);
    }

    public static boolean isProperHabitat(BlockGetter reader, BlockPos pos) {
        return reader.getBlockState(pos).isSolid();
    }

    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        this.tryTrample(worldIn, pos, entityIn, 100);
        super.stepOn(worldIn, pos, state, entityIn);
    }

    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        if (!(entityIn instanceof Zombie)) {
            this.tryTrample(worldIn, pos, entityIn, 3);
        }

        super.fallOn(worldIn, state, pos, entityIn, fallDistance);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return voxelShape;
    }

    private void tryTrample(Level worldIn, BlockPos pos, Entity trampler, int chances) {
        if (this.canTrample(worldIn, trampler)) {
            if (!worldIn.isClientSide && worldIn.random.nextInt(chances) == 0) {
                AABB bb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(25, 25, 25);
                if (trampler instanceof LivingEntity) {
                    List<Mob> list = worldIn.getEntitiesOfClass(Mob.class, bb, living -> living.isAlive() && living.getType() == births.get());
                    for (Mob living : list) {
                        if (!(living instanceof TamableAnimal) || !((TamableAnimal)living).isTame() || !((TamableAnimal)living).isOwnedBy((LivingEntity) trampler)) {
                            living.setTarget((LivingEntity) trampler);
                        }
                    }
                }
                BlockState blockstate = worldIn.getBlockState(pos);
                this.removeOneEgg(worldIn, pos, blockstate);

            }

        }
    }

    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        if (this.canGrow(worldIn) && isProperHabitat(worldIn, pos)) {
            int i = state.getValue(HATCH);
            if (i < 2) {
                worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
                worldIn.setBlock(pos, state.setValue(HATCH, Integer.valueOf(i + 1)), 2);
            } else {
                worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
                worldIn.removeBlock(pos, false);
                for (int j = 0; j < getDinosaursBornFrom(state); ++j) {
                    worldIn.levelEvent(2001, pos, Block.getId(state));
                    Entity fromType = births.get().create(worldIn);
                    if(fromType instanceof Animal animal){
                        animal.setAge(-24000);
                    }
                    fromType.moveTo((double) pos.getX() + 0.3D + (double) j * 0.2D, pos.getY(), (double) pos.getZ() + 0.3D, 0.0F, 0.0F);
                    if (!worldIn.isClientSide) {
                        Player closest = worldIn.getNearestPlayer(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 20, EntitySelector.NO_SPECTATORS);
                        if (closest != null) {
                            if(fromType instanceof TamableAnimal tamableAnimal){
                                tamableAnimal.setTame(true);
                                tamableAnimal.setOrderedToSit(true);
                                tamableAnimal.tame(closest);
                            }
                        }
                        worldIn.addFreshEntity(fromType);
                    }
                }
            }
        }

    }

    protected boolean canGrow(Level worldIn) {
        return worldIn.random.nextInt(10) == 0;
    }

    protected int getDinosaursBornFrom(BlockState state) {
        return 1;
    }

    protected void removeOneEgg(Level worldIn, BlockPos pos, BlockState state) {
        worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.random.nextFloat() * 0.2F);
        worldIn.destroyBlock(pos, false);
    }

    private boolean canTrample(Level worldIn, Entity trampler) {
        if (!trampler.getType().is(ACTagRegistry.DINOSAURS)) {
            if (!(trampler instanceof LivingEntity)) {
                return false;
            } else {
                return trampler instanceof Player || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, trampler);
            }
        } else {
            return false;
        }
    }

    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (isProperHabitat(worldIn, pos) && !worldIn.isClientSide) {
            worldIn.levelEvent(2005, pos, 0);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH);
    }

    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        this.removeOneEgg(worldIn, pos, state);
    }
}
