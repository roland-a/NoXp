// 
// Decompiled by Procyon v0.5.36
// 

package unaverage.no_xp.repair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import unaverage.no_xp.util.Helper;

import java.util.Map;

final class RepairWithBook
{
    static RepairOutput run(RepairInput ev) {
        if (!ev.right.inner.getItem().equals(Items.ENCHANTED_BOOK)) return null;

        ItemWrapper original = ev.left;
        ItemWrapper book = ev.right;
        ItemWrapper result;

        Map<Enchantment, Integer> newEnchantments = Helper.mergeEnchantmentMap(
            original.enchantments(),
            book.enchantments()
        );

        result = original;
        result = result.setEnchantments(newEnchantments);

        return new RepairOutput(result);
    }
}
