package unaverage.no_xp.events;

import net.minecraft.block.Block;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import unaverage.no_xp.NoXp;

@Mod.EventBusSubscriber(modid = NoXp.MOD_ID)
public final class XpEvents {
    //This is the level players start with
    //Cannot be 0 as repairs always need some levels of xp
    private static final int STARTING_LEVEL = 8;
    
    private XpEvents(){}

    @SubscribeEvent
    public static void deleteXpOrb(EntityJoinWorldEvent e){
        if (e.getEntity() instanceof ExperienceOrbEntity){
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void preventXpChange(PlayerXpEvent.XpChange e){
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void disableEnchantingTable(PlayerInteractEvent.RightClickBlock e){
        BlockPos pos = e.getPos();
        Block block = e.getWorld().getBlockState(pos).getBlock();

        if (!(block instanceof EnchantingTableBlock)) return;

        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void playerStartWithLevel(EntityJoinWorldEvent e){
        if (!(e.getEntity() instanceof PlayerEntity)) return;

        ((PlayerEntity)e.getEntity()).experienceLevel = STARTING_LEVEL;
    }

    @SubscribeEvent
    public static void resetLevelAfterRepair(AnvilRepairEvent e){
        e.getPlayer().experienceLevel = STARTING_LEVEL;
    }
}