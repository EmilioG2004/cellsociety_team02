package cellsociety.controller.services;

import static cellsociety.view.constants.SimulationUiConstants.*;

import cellsociety.controller.multiview.SimulationSlot;
import cellsociety.view.SimulationView;
import cellsociety.view.dialogs.PopulationGraphDialog;
import java.util.List;

/**
 * Service for simulation-specific dialog workflows (about and population graph dialogs).
 */
public class SimulationDialogService {

  private final SimulationView mySimView;
  private final PopulationGraphDialog myPopulationGraphDialog;

  public SimulationDialogService(SimulationView simView) {
    mySimView = simView;
    myPopulationGraphDialog = new PopulationGraphDialog();
  }

  /**
   * Opens the about dialog for the selected slot.
   */
  public void showAboutForSlot(List<SimulationSlot> slots, int slotIndex) {
    SimulationSlot slot = getRequiredLoadedSlot(slots, slotIndex);
    String currentNeighborStrategy = slot.getPresenter()
        .getDriver()
        .getSimulation()
        .getControllerGrid()
        .getNeighborStrategy()
        .getClass()
        .getSimpleName();
    mySimView.showAboutSplash(slot.getConfig(), currentNeighborStrategy);
  }

  /**
   * Pauses all simulations and opens the population graph dialog for the selected slot.
   */
  public void showPopulationGraphForSlot(List<SimulationSlot> slots, int slotIndex) {
    SimulationSlot slot = getRequiredLoadedSlot(slots, slotIndex);
    for (SimulationSlot current : slots) {
      current.pause();
    }
    myPopulationGraphDialog.showForSlot(slotIndex, slot.getPresenter().getPopulationHistory());
  }

  private SimulationSlot getRequiredLoadedSlot(List<SimulationSlot> slots, int slotIndex) {
    checkSlotIndex(slots, slotIndex);
    SimulationSlot slot = slots.get(slotIndex);
    if (!slot.hasSimulation()) {
      throw new IllegalStateException(ERROR_MSG_NO_SIMULATION_FOR_SLOT_PREFIX + slotIndex);
    }
    return slot;
  }

  private void checkSlotIndex(List<SimulationSlot> slots, int slotIndex) {
    if (slotIndex < 0 || slotIndex >= slots.size()) {
      throw new IndexOutOfBoundsException(ERROR_MSG_INVALID_SLOT_INDEX_PREFIX + slotIndex);
    }
  }
}
