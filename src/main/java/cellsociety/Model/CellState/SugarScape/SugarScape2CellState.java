package cellsociety.Model.CellState.SugarScape;

import cellsociety.Model.CellState.CellState;

/**
 * Cell state for SugarScape2 (reproduction preset). Extends SugarScapeCellState
 * to differentiate agent display by gender and fertility status:
 * <ul>
 *   <li>{@code -1} male agent (blue)</li>
 *   <li>{@code -2} female agent (red)</li>
 *   <li>{@code -3} agent past fertile age (grey)</li>
 * </ul>
 *
 * @author Michael Lei
 */
public class SugarScape2CellState extends SugarScapeCellState {

  public static final int MALE_AGENT_INT = -1;
  public static final int FEMALE_AGENT_INT = -2;
  public static final int POST_FERTILE_AGENT_INT = -3;

  public SugarScape2CellState(Patch patch, Agent2 agent) {
    super(patch, agent);
  }

  @Override
  public int toInt() {
    if (getAgent() == null) {
      return getPatchSugar();
    }
    if (getAgent() instanceof Agent2 a2) {
      if (a2.isPastFertileAge()) {
        return POST_FERTILE_AGENT_INT;
      }
      return a2.getGender() == Agent2.Gender.MALE ? MALE_AGENT_INT : FEMALE_AGENT_INT;
    }
    return MALE_AGENT_INT;
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
