package unaverage.no_xp.repair;

import com.google.common.collect.Sets;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.junit.jupiter.api.Test;
import unaverage.no_xp.config.ServerConfig;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static unaverage.no_xp.repair.Util.*;

public class RepairTest {

    @Test
    //test how much material is required to fully repair an item
    public void testMaterialCount(){
        resetConfig();

        testMaterialCount(SHOVEL, 1);
        testMaterialCount(SWORD, 1);
        testMaterialCount(PICK, 2);
        testMaterialCount(HELMET, 3);
        testMaterialCount(LEGGINGS, 4);
        testMaterialCount(CHEST_PLATE, 4);
    }

    private void testMaterialCount(ItemWrapper item, int expectedCount){
        {
            ServerConfig.overrideMaterialCount = true;

            RepairInput in = new RepairInput(
                item.almostDestroy(),
                INGOT.setCount(64)
            );

            RepairOutput result = in.runRepair(ServerLifecycleHooks.getCurrentServer().overworld());

            assertNotNull(result);
            assertEquals(expectedCount, result.materialCost);
        }

        //uses the vanilla default of always requiring 4 materials to fully repair
        {
            ServerConfig.overrideMaterialCount = false;

            RepairInput in = new RepairInput(
                item.almostDestroy(),
                INGOT.setCount(64)
            );

            RepairOutput result = in.runRepair(ServerLifecycleHooks.getCurrentServer().overworld());

            assertNotNull(result);
            assertEquals(4, result.materialCost);
        }
    }

    @Test
    public void testAddingEnchantments(){
        resetConfig();

        ItemWrapper item = SWORD;
        Enchantment e = Enchantments.SHARPNESS;

        Set<RepairInput> possibilities;

        possibilities = Sets.newHashSet(
            new RepairInput(item, item),
            new RepairInput(item, BOOK)
        );

        possibilities = map(
            possibilities,
            left -> left,
            right -> right.addEnchantment(e, 1)
        );

        RepairOutput result = assertAllRunSame(possibilities);

        assertEquals(item.addEnchantment(e, 1), result.output);
    }

    //tests the expected behaviors when repairing two items with the same enchantments
    //there are two cases when mixing levels
    //if both items have the same levels and is below the max level, then the resulting item's level will increment
    //otherwise, the resulting item will get the highest level of the two
    @Test
    public void testMergingLevels(){
        resetConfig();

        //case one
        testMergingLevels(1, 1, 2);
        testMergingLevels(2, 2, 3);

        //case two
        testMergingLevels(1, 2, 2);
        testMergingLevels(2, 1, 2);
        testMergingLevels(5, 5, 5);
    }

    private void testMergingLevels(int lvl1, int lvl2, int expectedLvl){
        ItemWrapper item = SWORD;
        Enchantment e = Enchantments.SHARPNESS;

        Set<RepairInput> possibilities;

        possibilities = Sets.newHashSet(
            new RepairInput(item, item),
            new RepairInput(item, BOOK)
        );

        possibilities = permute(possibilities);

        possibilities = map(
            possibilities,
            left -> left.addEnchantment(e, lvl1),
            right -> right.addEnchantment(e, lvl2)
        );

        RepairOutput result = assertAllRunSame(possibilities);

        assertEquals(item.addEnchantment(e, expectedLvl), result.output);
    }

    @Test
    //tests that if enchantments in an anvil conflicts, then the enchantments on the left takes priority
    public void testConflicting(){
        resetConfig();

        ItemWrapper item = PICK;
        Enchantment e1 = Enchantments.SILK_TOUCH;
        Enchantment e2 = Enchantments.BLOCK_FORTUNE;

        //e1 takes priority
        {
            Set<RepairInput> possibilities;

            possibilities = Sets.newHashSet(
                new RepairInput(item, item),
                new RepairInput(item, BOOK)
            );

            possibilities = permute(possibilities);

            possibilities = map(
                possibilities,
                left -> left.addEnchantment(e1, 1),
                right -> right.addEnchantment(e2, 1)
            );

            RepairOutput result = assertAllRunSame(possibilities);

            assertEquals(result.output, item.addEnchantment(e1, 1));
        }

        //e2 takes priority
        {
            Set<RepairInput> possibilities;

            possibilities = Sets.newHashSet(
                new RepairInput(item, item),
                new RepairInput(item, BOOK)
            );

            possibilities = permute(possibilities);

            possibilities = map(
                possibilities,
                left -> left.addEnchantment(e2, 1),
                right -> right.addEnchantment(e1, 1)
            );

            RepairOutput result = assertAllRunSame(possibilities);

            assertEquals(result.output, item.addEnchantment(e2, 1));
        }
    }

    @Test
    //Tests that incompatible enchantments never appear in the resulting item
    public void testIncompatible(){
        resetConfig();

        ItemWrapper item = SWORD;
        Enchantment e = Enchantments.BLAST_PROTECTION;

        RepairInput in = new RepairInput(
            item,
            BOOK.addEnchantment(e, 1)
        );

        RepairOutput result = in.runRepair(ServerLifecycleHooks.getCurrentServer().overworld());

        assertNotNull(result);
        assertFalse(result.output.enchantments().containsKey(e));
    }

    @Test
    //tests that unenchanted items do not lose max durability
    public void testNoDecay(){
        resetConfig();
        ServerConfig.decayRate = 0.1;

        ItemWrapper original = PICK;
        ItemWrapper result = original.almostDestroy().repair(Integer.MAX_VALUE);

        assertEquals(
            original.maxDurability(),
            result.maxDurability()
        );
    }

    @Test
    //tests that max durability decay works close enough to expected
    //cant be exact as durability can never go below one
    public void testDecayRate(){
        resetConfig();
        ServerConfig.decayRate = 0.1;

        ItemWrapper original = SAMPLE_DECAYABLE_TOOL;
        ItemWrapper result = original;

        for (int i = 1; i <= 5; i++){
            result = result.almostDestroy().repair(Integer.MAX_VALUE);

            assertEquals(
                original.maxDurability() * Math.pow(1-ServerConfig.decayRate, i),
                result.maxDurability(),
                i,
                i + " " + result.totalRepairs()
            );
        }
    }

    @Test
    //tests that max durability never goes below the max decay percent
    public void decayNeverBelowMax(){
        resetConfig();
        ServerConfig.maxDecay = .5;
        ServerConfig.decayRate = .1;

        ItemWrapper item = SAMPLE_DECAYABLE_TOOL;

        ItemWrapper result = item;

        for (int i = 1; i <= 100; i++){
            result = result.almostDestroy().repair(Integer.MAX_VALUE);
        }

        assertEquals(
            Math.round(item.maxDurability()*ServerConfig.maxDecay),
            result.maxDurability()
        );
    }

    @Test
    //tests that max durability never goes below one even if max decay is zero
    public void decayNeverBelow0(){
        resetConfig();
        ServerConfig.decayRate = 0.1;
        ServerConfig.maxDecay = 0.0;

        ItemWrapper result = SAMPLE_DECAYABLE_TOOL;

        for (int i = 1; i <= 100; i++){
            result = result.almostDestroy().repair(Integer.MAX_VALUE);
        }

        assertEquals(1, result.maxDurability());
    }

}


