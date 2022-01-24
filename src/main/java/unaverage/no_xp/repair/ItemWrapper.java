package unaverage.no_xp.repair;

import unaverage.no_xp.util.Helper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static unaverage.no_xp.config.ServerConfig.*;


public final class ItemWrapper {
    public static final String TOTAL_REPAIR_TAG = "no_xp:total_repairs";
    final ItemStack inner;

    public ItemWrapper(ItemStack inner) {
        this.inner = inner;
    }

    public ItemWrapper(Item inner) {
        this.inner = inner.getDefaultInstance();
    }
    
    public int count(){
        return inner.getCount();
    }

    public boolean canDecay() {
        //anything without durability cannot decay
        if (inner.getMaxDamage() == 0) return false;

        //only enchanted items are allowed to decay
        if (enchantments().isEmpty()) return false;

        //decay cant occur if the configs dont allow it
        if (decayRate == 0 || maxDecay == 1) return false;

        return true;
    }

    //Returns how much durability it has left
    public int maxDurability() {
        int totalRepairs = totalRepairs();
        int initDurability = inner.getMaxDamage();

        if (!canDecay()) return initDurability;

        //uses the finite geometric series, excluding the first term
        //   s=ar^1+ar^2+...+ar^n
        //or s=a(1-r^(n+1))/(1-r)-a
        //where
        //a is the initial durability
        //r is the decay rate
        //n is the total full repairs done
        //s is the total durability repaired

        //the formula for maxDurability is m=ar^n

        //solve for n in the first equation,
        //then plug it in on the second equation,
        //then simplify to get this result
        double result = initDurability - (decayRate/(1-decayRate))*totalRepairs;

        if (result < initDurability*maxDecay){
            result = initDurability*maxDecay;
        }
        //durability should never be 0
        if (result < 1){
            result = 1;
        }

        return (int)Math.round(result);
    }

    public int remainingDurability() {
        return inner.getMaxDamage() - inner.getDamageValue();
    }

    //Returns true if it cant be repaired anymore
    public boolean isFullyRepaired() {
        return remainingDurability() >= maxDurability();
    }

    public double durabilityPercent() {
        return remainingDurability() / (double) maxDurability();
    }

    public Map<Enchantment, Integer> enchantments(){
        return EnchantmentHelper.getEnchantments(inner);
    }

    ItemWrapper repair(int amount) {
        ItemWrapper result = this;

        for (int i = 0; i < amount; i++) {
            if (result.isFullyRepaired()) break;

            result = result.repairOnce();
        }

        return result;
    }

    private ItemWrapper repairOnce(){
        if (isFullyRepaired()) return this;

        ItemStack newInner = inner.copy();

        newInner.setDamageValue(
            newInner.getDamageValue()-1
        );

        if (canDecay()){
            newInner.getOrCreateTag().putInt(
                TOTAL_REPAIR_TAG,
                newInner.getOrCreateTag().getInt(TOTAL_REPAIR_TAG) + 1
            );
        }

        return new ItemWrapper(newInner);
    }

    //The total durability that has been repaired while the item was decayable
    public int totalRepairs() {
        return inner.getOrCreateTag().getInt(TOTAL_REPAIR_TAG);
    }

    //Returns a copy of the item with one durability left
    public ItemWrapper almostDestroy(){
        ItemStack newInner = inner.copy();

        newInner.setDamageValue(
            newInner.getMaxDamage()-1
        );

        return new ItemWrapper(newInner);
    }

    public ItemWrapper setCount(int count){
        ItemStack copy = inner.copy();

        copy.setCount(count);

        return new ItemWrapper(copy);
    }

    public ItemWrapper setEnchantments(Map<Enchantment, Integer> enchantments){
        enchantments = new HashMap<>(enchantments);

        ItemStack newItem = inner.copy();

        Helper.filterIncompatible(enchantments, newItem.getItem());

        EnchantmentHelper.setEnchantments(enchantments, newItem);

        return new ItemWrapper(newItem);
    }

    public ItemWrapper addEnchantment(Enchantment e, int level){
        Map<Enchantment, Integer> result;

        result = new HashMap<Enchantment, Integer>() {{
            put(e, level);
        }};

        result = Helper.mergeEnchantmentMap(
            enchantments(),
            result
        );

        return setEnchantments(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemWrapper that = (ItemWrapper) o;
        return Helper.itemEquals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Helper.itemHashed(inner);
    }

    @Override
    public String toString() {
        return "Item{" + inner.getItem() + ", " + inner.getOrCreateTag() + "}";
    }
}
