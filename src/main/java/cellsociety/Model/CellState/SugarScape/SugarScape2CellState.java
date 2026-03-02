package cellsociety.Model.CellState.SugarScape;

import cellsociety.Model.CellState.CellState;

/**
 * Cell state for SugarScape2 (reproduction preset). Extends SugarScapeCellState
 * to differentiate agent display by gender and fertility status.
 * Agent toInt(): 100 = male, 101 = female, 102 = past fertile age (all &gt;= 100 to avoid patch sugar 0..99).
 *
 * @author Michael Lei
 */
public class SugarScape2CellState extends SugarScapeCellState {

  public SugarScape2CellState(Patch patch, Agent2 agent) {
    super(patch, agent);
  }

  @Override
  public int toInt() {
    if (getAgent() == null) {
      return getPatchSugar();
    }
    if (getAgent() instanceof Agent2 a2) {
      if (a2.isPastFertileAge()) return 102;
      return a2.getGender() == Agent2.Gender.MALE ? 100 : 101;
    }
    return 100;
  }

  @Override
  public CellState getNextInCycle() {
    if (getAgent() == null) {
      return new SugarScape2CellState(new Patch(getPatchSugar()), new Agent2(
          null, null, null, null, null, null, null, null, null, null));
    }
    return new SugarScape2CellState(new Patch(getPatchSugar()), null);
  }
}
