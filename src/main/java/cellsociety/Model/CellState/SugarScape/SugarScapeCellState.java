package cellsociety.Model.CellState.SugarScape;

import cellsociety.Model.CellState.CellState;

/**
 * CellState for the SugarScape simulation. Wraps a {@link Patch} and an optional {@link Agent}.
 * A cell is vacant if it has no agent; otherwise it is occupied.
 *
 * @author Michael Lei
 */
public class SugarScapeCellState implements CellState {

  /** Representative instance for a cell with no agent. */
  public static final SugarScapeCellState NO_AGENT = new SugarScapeCellState(new Patch(), null);
  /** Representative instance for a cell with an agent. */
  public static final SugarScapeCellState HAS_AGENT = new SugarScapeCellState(new Patch(), new Agent());

  private Agent agent;
  private final Patch patch;

  /** Creates a cell state with the given patch and agent (null for vacant). */
  public SugarScapeCellState(Patch patch, Agent agent) {
    this.patch = patch;
    this.agent = agent;
  }

  /** Returns the sugar patch at this cell. */
  public Patch getPatch() {
    return this.patch;
  }

  /** Returns the current sugar level of this cell's patch. */
  public int getPatchSugar() {
    return this.patch.getSugar();
  }

  /** Returns the agent occupying this cell, or null if vacant. */
  public Agent getAgent() {
    return this.agent;
  }

  /** Returns true if no agent occupies this cell. */
  public boolean isVacant() {
    return this.agent == null;
  }

  /** Sets the agent at this cell (null to make vacant). */
  public void setAgent(Agent agent) {
    this.agent = agent;
  }

  /** Returns a new state with the same patch (copied) and no agent. Used so the next grid has its own state instances. */
  public SugarScapeCellState copyWithoutAgent() {
    return new SugarScapeCellState(patch.copy(), null);
  }

  /** Patch sugar 0..99; agent is 100 (avoids conflict with patch states). */
  @Override
  public int toInt() {
    if (agent != null) return 100;
    return getPatchSugar();
  }

  @Override
  public CellState getNextInCycle() {
    if (agent == null) return new SugarScapeCellState(new Patch(), new Agent());
    return new SugarScapeCellState(new Patch(patch.getSugar()), null);
  }
}
