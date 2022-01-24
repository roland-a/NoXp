package unaverage.no_xp.repair;

import net.minecraftforge.event.AnvilUpdateEvent;

import java.util.Objects;

public final class RepairOutput {
    public final ItemWrapper output;
    public final int materialCost;

    RepairOutput(ItemWrapper output, int materialCost) {
        this.output = output;
        this.materialCost = materialCost;
    }

    RepairOutput(ItemWrapper output) {
        this.output = output;
        this.materialCost = 0;
    }

    public void setEventOutput(AnvilUpdateEvent ev) {
        ev.setOutput(output.inner);
        ev.setCost(1);
        ev.setMaterialCost(materialCost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairOutput that = (RepairOutput) o;
        return materialCost == that.materialCost && Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(output, materialCost);
    }

    @Override
    public String toString() {
        return "RepairOutput{" +
            "output=" + output +
            (materialCost != 0 ? ", materialCost=" + materialCost : "") +
            '}';
    }
}

