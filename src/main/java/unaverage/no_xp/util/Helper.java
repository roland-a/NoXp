package unaverage.no_xp.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import net.minecraft.enchantment.Enchantment;

import java.util.HashSet;
import java.util.Map;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public final class Helper
{
    private Helper() {
    }




    public static Map<Enchantment, Integer> mergeEnchantmentMap(Map<Enchantment, Integer> leftMap, Map<Enchantment, Integer> rightMap){
        Map<Enchantment, Integer> resultMap = new HashMap<>();

        for (Enchantment e: leftMap.keySet()){
            int leftLvl = leftMap.get(e);
            int rightLvl = rightMap.getOrDefault(e, 0);

            assert leftLvl != 0;

            int resultLvl;

            if (leftLvl == rightLvl){
                resultLvl = leftLvl + 1;

                if (resultLvl > e.getMaxLevel()){
                    resultLvl = e.getMaxLevel();
                }
            }
            else {
                resultLvl = Math.max(leftLvl, rightLvl);
            }

            resultMap.put(e, resultLvl);
        }
        for (Enchantment e: rightMap.keySet()){
            if (leftMap.containsKey(e)) continue;

            if (!conflictsWithAny(e, leftMap)) continue;

            resultMap.put(e, rightMap.get(e));
        }
        return resultMap;
    }

    private static boolean conflictsWithAny(Enchantment e, Map<Enchantment, ?> map){
        for (Enchantment e2: map.keySet()){
            if (!e.isCompatibleWith(e2)) return false;
        }
        return true;
    }

    public static void filterIncompatible(Map<Enchantment, Integer> enchantments, Item item) {
        if (item == Items.ENCHANTED_BOOK) return;

        for (Enchantment e : new HashSet<>(enchantments.keySet())) {
            if (!e.category.canEnchant(item)) enchantments.remove(e);
        }
    }

    public static int itemHashed(ItemStack item){
        return Objects.hash(
            item.getItem(),
            item.getCount(),
            item.getDisplayName(),
            item.getOrCreateTag()
        );
    }

    public static boolean itemEquals(ItemStack left, ItemStack right){
        if (left.getItem() != right.getItem()) return false;

        if (left.getCount() != right.getCount()) return false;

        if (!left.getDisplayName().equals(right.getDisplayName())) return false;

        if (!left.getOrCreateTag().equals(right.getOrCreateTag())) return false;

        return true;
    }

    //private static boolean equals(Map<CompoundNBT, IBNT>)
}