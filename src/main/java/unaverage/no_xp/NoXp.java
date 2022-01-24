package unaverage.no_xp;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import unaverage.no_xp.config.ClientConfig;
import unaverage.no_xp.config.ServerConfig;

import static unaverage.no_xp.NoXp.MOD_ID;

@Mod(MOD_ID)
public final class NoXp
{
    public static final String MOD_ID = "no_xp";

    public NoXp() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }
}
