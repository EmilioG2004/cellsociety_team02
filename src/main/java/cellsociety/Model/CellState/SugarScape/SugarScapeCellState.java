package cellsociety.Model.CellState.SugarScape;

import cellsociety.Model.CellState.CellState;

/**
 * CellState for the SugarScape simulation. Wraps a {@link Patch} and an optional {@link Agent}.
 * A cell is vacant if it has no agent; otherwise it is occupied.
 *
 * @author Michael Lei
 */
public class SugarScapeCellState implements CellState {

  /** Int encoding for config and fromInt. */
  public static final int NO_SUGAR_INT = 0;
  public static final int MAX_SUGAR_INT = 1;
  public static final int HAS_AGENT_INT = 2;

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

  @Override
  public int toInt() {
    if (agent != null) return -1;
    return getPatchSugar();
  }

  @Override
  public CellState getNextInCycle() {
    if (agent == null) return new SugarScapeCellState(new Patch(), new Agent());
    return new SugarScapeCellState(new Patch(patch.getSugar()), null);
  }
}
