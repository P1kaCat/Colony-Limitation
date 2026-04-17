package com.p1kacat.colonylimitation;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_COLONIES_GLOBAL = BUILDER
            .comment("Maximum number of MineColonies colonies allowed on the server. Set to 0 to disable the limit.")
            .defineInRange("maxColoniesGlobal", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue DEV_MODE_ALLOW_MULTIPLE_COLONIES_PER_PLAYER = BUILDER
            .comment("Dev mode: allow multiple colonies per player (bypasses MineColonies restriction).")
            .define("devModeAllowMultipleColoniesPerPlayer", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
