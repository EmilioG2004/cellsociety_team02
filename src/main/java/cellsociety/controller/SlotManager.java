package cellsociety.controller;

import cellsociety.view.SimulationPanel;
import cellsociety.view.SimulationView;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.IntConsumer;

/**
 * Manages simulation slot lifecycle and dashboard tile updates.
 */
public class SlotManager {

  private final SimulationView mySimView;

  public SlotManager(SimulationView simView) {
    mySimView = simView;
  }

  /**
   * Resizes slot list to requested count and wires callbacks for newly created panels.
   */
  public void setSlotCount(List<SimulationSlot> slots, int desired, int min, int max,
      ResourceBundle bundle, IntConsumer onLoad, IntConsumer onAbout, IntConsumer onSave,
      IntConsumer onViewGraph) {
    int count = clamp(desired, min, max);

    while (slots.size() > count) {
      SimulationSlot removed = slots.remove(slots.size() - 1);
      removed.pause();
    }

    while (slots.size() < count) {
      int index = slots.size();
      SimulationPanel panel = new SimulationPanel(bundle);
      panel.setOnLoad(() -> onLoad.accept(index));
      panel.setOnAbout(() -> onAbout.accept(index));
      panel.setOnSave(() -> onSave.accept(index));
      panel.setOnViewGraph(() -> onViewGraph.accept(index));
      SimulationPresenter presenter = new SimulationPresenter(panel);
      SimulationSlot slot = new SimulationSlot(index, panel, presenter);
      slots.add(slot);
    }

    rebuildTileChildren(slots);
  }

  public void rebuildTileChildren(List<SimulationSlot> slots) {
    mySimView.getMultiSimTile().getChildren().clear();
    for (SimulationSlot slot : slots) {
      mySimView.getMultiSimTile().getChildren().add(slot.getNode());
    }
  }

  public void checkIndex(List<SimulationSlot> slots, int slotIndex) {
    if (slotIndex < 0 || slotIndex >= slots.size()) {
      throw new IndexOutOfBoundsException("Invalid slot index: " + slotIndex);
    }
  }

  private int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
