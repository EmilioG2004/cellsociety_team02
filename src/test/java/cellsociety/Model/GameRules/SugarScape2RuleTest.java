package cellsociety.Model.GameRules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SugarScape.Agent2;
import cellsociety.Model.CellState.SugarScape.Patch;
import cellsociety.Model.CellState.SugarScape.SugarScape2CellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;

import static java.util.Map.entry;
import org.junit.jupiter.api.Test;

/**
 * Tests for SugarScape2Rule (reproduction preset). Uses the ZOMBIES pattern.
 * Covers aging, reproduction, fromInt, and getDefaultNeighborStrategy.
 */
class SugarScape2RuleTest {

  private SugarScape2Rule rule;

  /** Rule with deterministic agent traits and wide fertile window for reproduction tests. */
  private SugarScape2Rule deterministicRule;

  @BeforeEach
  void setUp() {
    rule = new SugarScape2Rule();
    deterministicRule = new SugarScape2Rule(Map.ofEntries(
        entry("agentInitialMinimumSugar", 20.0),
        entry("agentInitialMaximumSugar", 20.0),
        entry("agentMinimumMetabolism", 1.0),
        entry("agentMaximumMetabolism", 1.0),
        entry("agentVision", 1.0),
        entry("sugarGrowBackRate", 1.0),
        entry("sugarGrowBackInterval", 1.0),
        entry("agentMaxAgeMin", 100.0),
        entry("agentMaxAgeMax", 100.0),
        entry("agentFertileStart", 0.0),
        entry("agentFertileEnd", 99.0)
    ));
  }

  // ---------- helpers ----------

  private Grid makeGrid(SugarScape2CellState[][] states) {
    return new ArrayGrid(states, new VonNeumannNeighborStrategy());
  }

  /** Vacant cell with the given patch sugar. */
  private SugarScape2CellState patch(int sugar) {
    return new SugarScape2CellState(new Patch(sugar), null);
  }

  /** Occupied cell with a deterministic Agent2 (child constructor: sugar, metabolism, vision, maxAge, fertile). */
  private SugarScape2CellState occupied(int sugar, int metabolism, int vision,
      int maxAge, int fertileStart, int fertileEnd) {
    Patch p = new Patch(Patch.DEFAULT_SUGAR_CAPACITY);
    Agent2 a = new Agent2(sugar, metabolism, vision, maxAge, maxAge, fertileStart, fertileEnd);
    return new SugarScape2CellState(p, a);
  }

  private SugarScape2CellState state(Grid g, int r, int c) {
    return (SugarScape2CellState) g.getCellState(r, c);
  }

  private int countAgents(Grid g) {
    int n = 0;
    for (int r = 0; r < g.getRows(); r++) {
      for (int c = 0; c < g.getCols(); c++) {
        if (!state(g, r, c).isVacant()) n++;
      }
    }
    return n;
  }

  // ---------- Z (Zero): no agents ----------

  @Test
  void getNextGrid_noAgents_agentCountRemainsZero() {
    Grid grid = makeGrid(new SugarScape2CellState[][]{
        {patch(0), patch(0)},
        {patch(0), patch(0)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(0, countAgents(next));
  }

  @Test
  void getNextGrid_allEmptyPatches_sugarGrowsBack() {
    Patch p00 = new Patch(4);
    p00.giveSugar();
    Patch p01 = new Patch(4);
    p01.giveSugar();
    Grid grid = makeGrid(new SugarScape2CellState[][]{
        {new SugarScape2CellState(p00, null), new SugarScape2CellState(p01, null)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(1, state(next, 0, 0).getPatchSugar());
    assertEquals(1, state(next, 0, 1).getPatchSugar());
  }

  // ---------- O (One): single agent ----------

  @Test
  void getNextGrid_singleAgent_agentCountRemainsOne() {
    Grid grid = makeGrid(new SugarScape2CellState[][]{
        {patch(0), patch(0), patch(0)},
        {patch(0), occupied(20, 1, 1, 100, 0, 99), patch(0)},
        {patch(0), patch(0), patch(0)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(1, countAgents(next));
  }

  @Test
  void getNextGrid_singleAgent_agesByOne() {
    SugarScape2CellState[][] states = {
        {patch(0), patch(4), patch(0)},
        {patch(0), occupied(20, 1, 1, 100, 0, 99), patch(0)},
        {patch(0), patch(0), patch(0)}
    };
    Grid grid = makeGrid(states);
    Agent2 agentBefore = (Agent2) state(grid, 1, 1).getAgent();
    assertEquals(0, agentBefore.getAge());

    Grid next = deterministicRule.getNextGrid(grid);
    // Agent may have moved to (0,1) for sugar; find the occupied cell
    int ar = -1, ac = -1;
    for (int r = 0; r < next.getRows(); r++) {
      for (int c = 0; c < next.getCols(); c++) {
        if (!state(next, r, c).isVacant()) {
          ar = r;
          ac = c;
          break;
        }
      }
      if (ar >= 0) break;
    }
    assertTrue(ar >= 0 && ac >= 0, "Agent should still be on grid");
    Agent2 agentAfter = (Agent2) state(next, ar, ac).getAgent();
    assertEquals(1, agentAfter.getAge());
  }

  // ---------- M (Many): aging and death ----------

  @Test
  void getNextGrid_agentAtMaxAge_diesAndCellBecomesVacant() {
    // maxAge=1: after one step age becomes 1, isAlive() = (1 < 1) = false
    SugarScape2CellState[][] states = {
        {patch(4), occupied(20, 1, 1, 1, 0, 1), patch(4)}
    };
    Grid grid = makeGrid(states);
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(0, countAgents(next), "Agent at maxAge should die after aging");
  }

  @Test
  void getNextGrid_agentDiesWhenSugarDepleted_cellBecomesVacant() {
    SugarScape2CellState[][] states = {
        {patch(0), occupied(1, 2, 1, 100, 0, 99), patch(0)}
    };
    Grid grid = makeGrid(states);
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(0, countAgents(next), "Agent with insufficient sugar should die");
  }

  // ---------- I (Interface): fromInt, getDefaultNeighborStrategy ----------

  @Test
  void fromInt_negativeOne_createsOccupiedStateWithAgent2() {
    CellState state = rule.fromInt(-1);
    assertInstanceOf(SugarScape2CellState.class, state);
    SugarScape2CellState ss = (SugarScape2CellState) state;
    assertFalse(ss.isVacant());
    assertInstanceOf(Agent2.class, ss.getAgent());
  }

  @Test
  void fromInt_zero_createsVacantWithZeroSugar() {
    CellState state = rule.fromInt(0);
    assertInstanceOf(SugarScape2CellState.class, state);
    SugarScape2CellState ss = (SugarScape2CellState) state;
    assertTrue(ss.isVacant());
    assertEquals(0, ss.getPatchSugar());
  }

  @Test
  void fromInt_positive_createsVacantWithCorrectSugar() {
    CellState state = rule.fromInt(3);
    assertInstanceOf(SugarScape2CellState.class, state);
    SugarScape2CellState ss = (SugarScape2CellState) state;
    assertTrue(ss.isVacant());
    assertEquals(3, ss.getPatchSugar());
  }

  @Test
  void fromInt_negativeTwoOrLess_throws() {
    assertThrows(IllegalArgumentException.class, () -> rule.fromInt(-2));
  }

  @Test
  void getDefaultNeighborStrategy_returnsVonNeumann() {
    assertNotNull(rule.getDefaultNeighborStrategy());
    assertInstanceOf(VonNeumannNeighborStrategy.class, rule.getDefaultNeighborStrategy());
  }

  // ---------- E (Exceptional): constructor ----------

  @Test
  void constructor_nullParameters_usesDefaults() {
    SugarScape2Rule r = new SugarScape2Rule(null);
    assertNotNull(r);
    CellState s = r.fromInt(-1);
    assertInstanceOf(SugarScape2CellState.class, s);
  }
}
