package unaverage.no_xp.events;

import unaverage.no_xp.NoXp;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import unaverage.no_xp.repair.*;

@Mod.EventBusSubscriber(modid = NoXp.MOD_ID)
public final class RepairEvent {
    @SubscribeEvent
    public static void onAnvilChange(AnvilUpdateEvent ev){
        if (ev.getLeft().isEmpty()) return;
        if (ev.getRight().isEmpty()) return;
        if (ev.getPlayer() == null) return;

        RepairInput repairInput = new RepairInput(ev);
        RepairOutput repairOutput = repairInput.runRepair(ev.getPlayer().level);

        if (repairOutput == null) return;

        //if either inputs are exactly the same as the output, then the repair is pointless
        if (repairInput.left.equals(repairOutput.output) || repairInput.right.equals(repairOutput.output)){
            ev.setCanceled(true);
        }

        repairOutput.setEventOutput(ev);

        setName(ev);
    }

    private static void setName(AnvilUpdateEvent ev) {
        if (StringUtils.isBlank(ev.getName())) {
            ev.getOutput().resetHoverName();
        } else {
            ev.getOutput().setHoverName(new StringTextComponent(ev.getName()));
        }
    }

}