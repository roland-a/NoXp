package unaverage.no_xp.config;

import unaverage.no_xp.NoXp;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.*;

@Mod.EventBusSubscriber(modid = NoXp.MOD_ID, bus = Bus.MOD)
public final class ServerConfig {
    public static Double decayRate = null;
    private static final ForgeConfigSpec.DoubleValue DECAY_RATE_SPEC;

    public static Double maxDecay = null;
    private static final ForgeConfigSpec.DoubleValue MAX_DECAY_SPEC;

    public static Boolean overrideMaterialCount = null;
    private static final ForgeConfigSpec.BooleanValue IMPROVED_MATERIAL_REPAIR_SPEC;

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DECAY_RATE_SPEC = builder
                .comment("\nThe percentage of maximum durability that gets lost for each full repair.")
                .defineInRange("decayRate", 5d, 0d, 100d);

        MAX_DECAY_SPEC = builder
                .comment("\nThe maximum amount of maximum durability that a tool can lose.")
                .defineInRange("decayLimit", 10d, 0d, 100d);

        IMPROVED_MATERIAL_REPAIR_SPEC = builder
                .comment(
                    "\nEnables overriding the amount of material needed to repair an item." +
                    "\nInstead of always requiring 4 materials to fully repair an item, you will only need half of the material needed to craft the item, rounded up." +
                    "\nChange this value to false to use the normal vanilla behavior."
                )
                .define("overrideMaterialCost", true);

        SPEC = builder.build();
    }

    @SubscribeEvent
    public static void onConfigEvent(ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    static void bake(){
        decayRate = DECAY_RATE_SPEC.get()/100;
        maxDecay = MAX_DECAY_SPEC.get()/100;
        overrideMaterialCount = IMPROVED_MATERIAL_REPAIR_SPEC.get();
    }
}