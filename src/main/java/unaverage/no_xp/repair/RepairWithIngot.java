// 
// Decompiled by Procyon v0.5.36
// 

package unaverage.no_xp.repair;

import net.minecraft.item.crafting.Ingredient;
import javax.annotation.Nullable;

import net.minecraft.item.crafting.IRecipe;
import unaverage.no_xp.config.ServerConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

final class RepairWithIngot {
    private RepairWithIngot() {}
    
    static RepairOutput run(RepairInput ev, World world) {
        if (!ev.left.inner.getItem().isValidRepairItem(ev.left.inner, ev.right.inner)) return null;

        ItemWrapper result = ev.left;

        int repairPerMaterial = getRepairPerMaterial(result, world);
        int materialCost = 0;

        for (int i = 1; i <= ev.right.count(); i++) {
            if (result.isFullyRepaired()) {
                break;
            }

            materialCost = i;

            result = result.repair(repairPerMaterial);
        }

        return new RepairOutput(result, materialCost);
    }
    
    private static int getRepairPerMaterial(ItemWrapper tool, World world) {
        int materialRepairCount = getMaterialRepairCount(tool.inner, world);

        tool = tool.almostDestroy();

        for (int i = 1;; i++){
            tool = tool.repair(1);

            if (tool.isFullyRepaired()){
                return (int)Math.ceil(i / (double)materialRepairCount);
            }
        }
    }
    
    private static int getMaterialRepairCount(ItemStack tool, World world) {
        Integer materialCraftingCount = getMaterialCraftingCount(tool, world);

        if (materialCraftingCount == null || !ServerConfig.overrideMaterialCount) {
            return 4;
        }

        return (int)Math.ceil(materialCraftingCount / 2.0);
    }
    
    @Nullable
    private static Integer getMaterialCraftingCount(ItemStack tool, World world) {
        for (IRecipe<?> recipe : world.getRecipeManager().getRecipes()) {
            Integer material = getMaterialCraftingCount(recipe, tool);

            if (material == null) continue;

            return material;
        }
        return null;
    }
    
    @Nullable
    private static Integer getMaterialCraftingCount(IRecipe<?> recipe, ItemStack tool) {
        int result = 0;

        if (recipe.getResultItem().getItem() != tool.getItem()) return null;

        for (Ingredient ingredient: recipe.getIngredients()){
            for (ItemStack item: ingredient.getItems()){
                //ignore tool recipes with itself as an ingredient
                if (item.getItem().equals(tool.getItem())) return null;

                if (!tool.getItem().isValidRepairItem(tool, item)) continue;

                result++;

                break;
            }
        }

        if (result == 0) return null;

        return result;
    }
}
