package unaverage.no_xp.repair;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import unaverage.no_xp.config.ServerConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Util {
    static final ItemWrapper SHOVEL = new ItemWrapper(Items.DIAMOND_SHOVEL);
    static final ItemWrapper SWORD = new ItemWrapper(Items.DIAMOND_SWORD);
    static final ItemWrapper PICK = new ItemWrapper(Items.DIAMOND_PICKAXE);
    static final ItemWrapper HELMET = new ItemWrapper(Items.DIAMOND_HELMET);
    static final ItemWrapper LEGGINGS = new ItemWrapper(Items.DIAMOND_LEGGINGS);
    static final ItemWrapper CHEST_PLATE = new ItemWrapper(Items.DIAMOND_CHESTPLATE);
    static final ItemWrapper INGOT = new ItemWrapper(Items.DIAMOND);
    static final ItemWrapper BOOK = new ItemWrapper(Items.ENCHANTED_BOOK);
    
    static final ItemWrapper SAMPLE_DECAYABLE_TOOL = PICK.addEnchantment(Enchantments.SILK_TOUCH, 1);


    //asserts that all the repair inputs becomes the same output when ran
    static RepairOutput assertAllRunSame(Set<RepairInput> repairInputs){
        if (repairInputs.isEmpty()){
            throw new RuntimeException("repair inputs is empty");
        }

        RepairOutput first = null;

        ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();

        for (RepairInput i: repairInputs){
            if (first == null){
                first = i.runRepair(world);
                break;
            }

            RepairOutput out = i.runRepair(world);

            if (!first.equals(out)) {
                throw new RuntimeException();
            }
        }

        return first;
    }

    static Set<RepairInput> map(
        Set<RepairInput> repairInputs,
        Function<ItemWrapper, ItemWrapper> left,
        Function<ItemWrapper, ItemWrapper> right
    ){
        Set<RepairInput> result = new HashSet<>();

        for (RepairInput r: repairInputs){
            result.add(
                new RepairInput(
                    left.apply(r.left),
                    right.apply(r.right)
                )
            );
        }

        return result;
    }

    static Set<RepairInput> permute(Set<RepairInput> repairInputs){
        Set<RepairInput> result = new HashSet<>();

        for (RepairInput i: repairInputs){
            result.add(
                new RepairInput(
                    i.left,
                    i.right
                )
            );
            result.add(
                new RepairInput(
                    i.left,
                    i.right
                )
            );
        }
        return result;
    }

    static void resetConfig(){
        ServerConfig.overrideMaterialCount = false;
        ServerConfig.decayRate = 0.0;
        ServerConfig.maxDecay = 0.0;
    }
}
