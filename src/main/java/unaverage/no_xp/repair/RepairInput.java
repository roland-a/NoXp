package unaverage.no_xp.repair;

import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;

import javax.annotation.Nullable;
import java.util.Objects;

public final class RepairInput {
    public final ItemWrapper left;
    public final ItemWrapper right;

    public RepairInput(AnvilUpdateEvent ev) {
        left = new ItemWrapper(ev.getLeft().copy());
        right = new ItemWrapper(ev.getRight().copy());
    }

    public RepairInput(ItemWrapper _left, ItemWrapper _right) {
        left = _left;
        right = _right;
    }

    @Nullable
    public RepairOutput runRepair(World world){
        RepairOutput result;

        result = RepairWithIngot.run(this, world);
        if (result != null) return result;

        result = RepairWithTool.run(this);
        if (result != null) return result;

        result = RepairWithBook.run(this);
        if (result != null) return result;

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairInput that = (RepairInput) o;
        return left.equals(that.left) && right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "RepairInput{" +
            "input=" + left +
            ", other=" + right +
            '}';
    }
}
