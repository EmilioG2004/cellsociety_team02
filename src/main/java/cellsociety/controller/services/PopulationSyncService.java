package cellsociety.controller.services;

import cellsociety.controller.multiview.SimulationSlot;
import java.util.List;

/**
 * Synchronizes per-slot population snapshots for all currently loaded simulations.
 */
public class PopulationSyncService {

  /**
   * Calls population sync on every slot that currently has an active simulation.
   *
   * @param slots simulation slots to synchronize
   */
  public void syncLoadedSlots(List<SimulationSlot> slots) {
    for (SimulationSlot slot : slots) {
      if (!slot.hasSimulation()) {
        continue;
      }
      slot.getPresenter().syncPopulationAtCurrentGeneration();
    }
  }
}
