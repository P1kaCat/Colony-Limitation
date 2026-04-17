package com.p1kacat.colonylimitation.mixin;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.core.network.messages.server.GetColonyInfoMessage;
import com.p1kacat.colonylimitation.Config;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GetColonyInfoMessage.class, remap = false)
public class GetColonyInfoMessageMixin {
    @Redirect(
            method = "onExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/minecolonies/api/colony/IColonyManager;getIColonyByOwner(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;)Lcom/minecolonies/api/colony/IColony;"
            ),
            remap = false
    )
    private IColony colonylimitation$allowMultipleColoniesInfo(IColonyManager instance, Level world, Player player) {
        if (Config.DEV_MODE_ALLOW_MULTIPLE_COLONIES_PER_PLAYER.get()) {
            return null;
        }
        return instance.getIColonyByOwner(world, player);
    }
}
