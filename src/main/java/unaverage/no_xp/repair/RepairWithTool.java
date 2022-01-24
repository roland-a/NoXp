package unaverage.no_xp.repair;

import net.minecraft.enchantment.Enchantment;
import unaverage.no_xp.util.Helper;

import java.util.Map;

final class RepairWithTool {
    private RepairWithTool() {}
    
    static RepairOutput run(RepairInput ev) {
        if (!ev.left.inner.isRepairable()) return null;
        if (!ev.left.inner.getItem().equals(ev.right.inner.getItem())) return null;

        ItemWrapper left = ev.left;
        ItemWrapper right = ev.right;

        //enchantments on the left should always be prioritize over enchantments on the right
        Map<Enchantment, Integer> newEnchantments = Helper.mergeEnchantmentMap(
            left.enchantments(),
            right.enchantments()
        );

        ItemWrapper result;
        result = mergeRepair(left, right);
        result = result.setEnchantments(newEnchantments);

        return new RepairOutput(result);
    }


    //The total repair of the resulting item should be independent of the order that the input is placed in the anvil
    private static ItemWrapper mergeRepair(ItemWrapper left, ItemWrapper right) {
        ItemWrapper leftAsBase = left.repair(right.remainingDurability());
        ItemWrapper rightAsBase = right.repair(left.remainingDurability());

        if (leftAsBase.totalRepairs() > rightAsBase.totalRepairs()){
            return leftAsBase;
        }
        else {
            return rightAsBase;
        }
    }
}
