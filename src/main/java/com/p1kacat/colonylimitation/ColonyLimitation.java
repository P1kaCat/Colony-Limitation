package com.p1kacat.colonylimitation;

import com.mojang.logging.LogUtils;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.workerbuildings.ITownHall;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.UUID;

@Mod(ColonyLimitation.MODID)
public class ColonyLimitation {
    public static final String MODID = "colonylimitation";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TOWN_HALL_BLOCK_ID = ResourceLocation.fromNamespaceAndPath("minecolonies", "blockhuttownhall");

    public ColonyLimitation(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);

        IMinecoloniesAPI.getInstance().getEventBus().subscribe(ColonyCreatedModEvent.class, this::onColonyCreated);
    }

    private void onColonyCreated(ColonyCreatedModEvent event) {
        final IColony colony = event.getColony();
        if (colony.isRemote()) {
            return;
        }

        final ServerLevel world = (ServerLevel) colony.getWorld();
        final int colonyId = colony.getID();
        final UUID ownerId = colony.getPermissions().getOwner();

        final int totalColonies = IColonyManager.getInstance().getAllColonies().size();
        final int maxGlobal = Config.MAX_COLONIES_GLOBAL.get();
        final boolean globalExceeded = maxGlobal > 0 && totalColonies > maxGlobal;
        if (!globalExceeded) {
            return;
        }

        final ServerPlayer owner = world.getServer().getPlayerList().getPlayer(ownerId);
        final BlockPos townHallPos = resolveTownHallPos(colony);

        if (townHallPos != null) {
            refundTownHall(world, townHallPos, owner);
        }

        IColonyManager.getInstance().deleteColonyByWorld(colonyId, false, world);

        if (owner != null) {
            owner.sendSystemMessage(Component.translatable("message.colonylimitation.limit_reached", maxGlobal));
        }

        LOGGER.info("Deleted colony {} because global limit {} was exceeded (total {}).", colonyId, maxGlobal, totalColonies);
    }

    private BlockPos resolveTownHallPos(IColony colony) {
        try {
            final ITownHall townHall = colony.getServerBuildingManager().getTownHall();
            if (townHall != null) {
                return townHall.getPosition();
            }
        } catch (Exception ignored) {
            // If MineColonies internals change, fall back to colony center.
        }
        return colony.getCenter();
    }

    private void refundTownHall(ServerLevel world, BlockPos pos, ServerPlayer owner) {
        if (!world.isLoaded(pos)) {
            return;
        }

        if (!BuiltInRegistries.BLOCK.containsKey(TOWN_HALL_BLOCK_ID)) {
            return;
        }

        final Block townHallBlock = BuiltInRegistries.BLOCK.get(TOWN_HALL_BLOCK_ID);
        if (!world.getBlockState(pos).is(townHallBlock)) {
            return;
        }

        final ItemStack stack = new ItemStack(townHallBlock);
        if (owner != null) {
            if (!owner.getInventory().add(stack)) {
                Containers.dropItemStack(world, owner.getX(), owner.getY(), owner.getZ(), stack);
            }
        } else {
            Containers.dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        }

        world.destroyBlock(pos, false);
    }
}
