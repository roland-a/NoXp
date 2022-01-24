package unaverage.no_xp.config;

import net.minecraftforge.api.distmarker.Dist;
import unaverage.no_xp.NoXp;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.*;

@Mod.EventBusSubscriber(modid = NoXp.MOD_ID, bus = Bus.MOD)
public final class ClientConfig {
    public static Boolean overrideDurabilityBar = null;
    private static final ForgeConfigSpec.BooleanValue DURABILITY_BAR_SPEC;

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DURABILITY_BAR_SPEC = builder
            .comment(
                "\nEnables overriding the durability bar make it relative to the current maximum durability." +
                "\nSet this value to false if it causes problems with other mods." +
                "\nChanging this value requires restarting your game to properly work."
            )
            .worldRestart()
            .define("overrideDurabilityBar", true);

        SPEC = builder.build();
    }

    @SubscribeEvent
    public static void onConfigEvent(ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    static void bake(){
        overrideDurabilityBar = DURABILITY_BAR_SPEC.get();
    }
}