package cellsociety.Model.GameRules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.CellState.SugarScape.Agent;
import cellsociety.Model.CellState.SugarScape.Patch;
import cellsociety.Model.CellState.SugarScape.SugarScapeCellState;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.NeighborStrategy.VonNeumannNeighborStrategy;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for SugarScapeRule. Uses the ZOMBIES pattern.
 * Rule tests use getNextGrid(Grid) with ArrayGrid + VonNeumannNeighborStrategy.
 */
class SugarScapeRuleTest {

  private SugarScapeRule rule;

  /** Rule with deterministic agent traits: sugar=10, metabolism=1, vision=1, growBack=1/tick. */
  private SugarScapeRule deterministicRule;

  @BeforeEach
  void setUp() {
    rule = new SugarScapeRule();
    deterministicRule = new SugarScapeRule(Map.of(
        "agentInitialMinimumSugar", 10.0,
        "agentInitialMaximumSugar", 10.0,
        "agentMinimumMetabolism", 1.0,
        "agentMaximumMetabolism", 1.0,
        "agentVision", 1.0,
        "sugarGrowBackRate", 1.0,
        "sugarGrowBackInterval", 1.0
    ));
  }

  // ---------- helpers ----------

  private Grid makeGrid(SugarScapeCellState[][] states) {
    return new ArrayGrid(states, new VonNeumannNeighborStrategy());
  }

  /** Returns a vacant cell with the given patch sugar. */
  private SugarScapeCellState patch(int sugar) {
    Patch p = new Patch(sugar);
    return new SugarScapeCellState(p, null);
  }

  /** Returns an occupied cell with a deterministic agent (sugar=s, metabolism=m, vision=v). */
  private SugarScapeCellState occupied(int sugar, int metabolism, int vision) {
    Patch p = new Patch(Patch.DEFAULT_SUGAR_CAPACITY);
    Agent a = new Agent(sugar, sugar, metabolism, metabolism, vision, vision);
    return new SugarScapeCellState(p, a);
  }

  private SugarScapeCellState state(Grid g, int r, int c) {
    return (SugarScapeCellState) g.getCellState(r, c);
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
    Grid grid = makeGrid(new SugarScapeCellState[][]{
        {patch(0), patch(0)},
        {patch(0), patch(0)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(0, countAgents(next));
  }

  @Test
  void getNextGrid_allEmptyPatches_sugarGrowsBack() {
    // Start with empty patches (capacity 4, sugar 0 after giveSugar)
    Patch p00 = new Patch(4); p00.giveSugar();
    Patch p01 = new Patch(4); p01.giveSugar();
    Patch p10 = new Patch(4); p10.giveSugar();
    Patch p11 = new Patch(4); p11.giveSugar();
    Grid grid = makeGrid(new SugarScapeCellState[][]{
        {new SugarScapeCellState(p00, null), new SugarScapeCellState(p01, null)},
        {new SugarScapeCellState(p10, null), new SugarScapeCellState(p11, null)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    // growBack rate=1, interval=1, tick=0 → all patches grow by 1
    assertEquals(1, state(next, 0, 0).getPatchSugar());
    assertEquals(1, state(next, 0, 1).getPatchSugar());
    assertEquals(1, state(next, 1, 0).getPatchSugar());
    assertEquals(1, state(next, 1, 1).getPatchSugar());
  }

  @Test
  void getNextGrid_fullPatches_noGrowBeyondCapacity() {
    Grid grid = makeGrid(new SugarScapeCellState[][]{
        {patch(4), patch(4)},
        {patch(4), patch(4)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(4, state(next, 0, 0).getPatchSugar());
    assertEquals(4, state(next, 1, 1).getPatchSugar());
  }

  // ---------- O (One): single agent ----------

  @Test
  void getNextGrid_singleAgent_agentCountRemainsOne() {
    // 3x3 grid: agent at center, all patches vacant
    Grid grid = makeGrid(new SugarScapeCellState[][]{
        {patch(0), patch(0), patch(0)},
        {patch(0), occupied(10, 1, 1), patch(0)},
        {patch(0), patch(0), patch(0)}
    });
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(1, countAgents(next));
  }

  @Test
  void getNextGrid_agentMovesToBestNeighbor_mostSugar() {
    // Agent at (1,1) with vision=1. Neighbor (0,1) has sugar=4 (max), rest have sugar=0.
    // Agent should move to (0,1).
    SugarScapeCellState[][] states = {
        {patch(0), patch(4), patch(0)},
        {patch(0), occupied(10, 1, 1), patch(0)},
        {patch(0), patch(0), patch(0)}
    };
    Grid grid = makeGrid(states);
    Grid next = deterministicRule.getNextGrid(grid);
    assertFalse(state(next, 0, 1).isVacant(), "Agent should move to (0,1) with highest sugar");
    assertTrue(state(next, 1, 1).isVacant(), "Agent should leave (1,1)");
  }

  @Test
  void getNextGrid_agentConsumesPatcSugar_patchBecomesZero() {
    // Agent at (0,0), only neighbor in a 1x2 grid is (0,1) with sugar=3
    SugarScapeCellState[][] states = {
        {occupied(10, 1, 1), patch(3)}
    };
    Grid grid = makeGrid(states);
    Grid next = deterministicRule.getNextGrid(grid);
    // After move the destination patch sugar is consumed (set to 0)
    int destPatchSugar = state(next, 0, 1).getPatchSugar();
    assertEquals(0, destPatchSugar, "Agent should consume patch sugar on arrival");
  }

  // ---------- M (Many): agents and their lifecycle ----------

  @Test
  void getNextGrid_agentDiesWhenSugarDepleted_cellBecomesVacant() {
    // Agent with sugar=1, metabolism=2 → dies after metabolize (1-2=-1 ≤ 0)
    SugarScapeCellState[][] states = {
        {patch(0), occupied(1, 2, 1), patch(0)},
    };
    Grid grid = makeGrid(states);
    Grid next = deterministicRule.getNextGrid(grid);
    assertEquals(0, countAgents(next), "Agent with insufficient sugar should die");
  }

  @Test
  void getNextGrid_multipleAgents_countPreservedWhenAllSurvive() {
    // Two agents with plenty of sugar, no overlap
    SugarScapeCellState[][] states = {
        {occupied(10, 1, 1), patch(0), occupied(10, 1, 1)},
        {patch(0), patch(0), patch(0)}
    };
    Grid grid = makeGrid(states);
    Grid next = deterministicRule.getNextGrid(grid);
    // Both agents should survive (sugar=10, metabolism=1 → sugar=9 after step, still > 0)
    assertEquals(2, countAgents(next));
  }

  // ---------- I (Interface): fromInt, getDefaultNeighborStrategy ----------

  @Test
  void fromInt_negativeOne_createsOccupiedState() {
    CellState state = rule.fromInt(-1);
    assertInstanceOf(SugarScapeCellState.class, state);
    assertFalse(((SugarScapeCellState) state).isVacant());
  }

  @Test
  void fromInt_zero_createsVacantWithZeroSugar() {
    CellState state = rule.fromInt(0);
    assertInstanceOf(SugarScapeCellState.class, state);
    SugarScapeCellState ss = (SugarScapeCellState) state;
    assertTrue(ss.isVacant());
    assertEquals(0, ss.getPatchSugar());
  }

  @Test
  void fromInt_positive_createsVacantWithCorrectSugar() {
    CellState state = rule.fromInt(3);
    assertInstanceOf(SugarScapeCellState.class, state);
    SugarScapeCellState ss = (SugarScapeCellState) state;
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

  // ---------- E (Exceptional): invalid / edge parameters ----------

  @Test
  void constructor_nullParameters_usesDefaults() {
    SugarScapeRule r = new SugarScapeRule(null);
    assertNotNull(r);
  }

  @Test
  void fromInt_agentHasVisionFromParameter() {
    SugarScapeRule r = new SugarScapeRule(Map.of(
        "agentVision", 3.0,
        "agentInitialMinimumSugar", 10.0, "agentInitialMaximumSugar", 10.0,
        "agentMinimumMetabolism", 1.0, "agentMaximumMetabolism", 1.0));
    SugarScapeCellState s = (SugarScapeCellState) r.fromInt(-1);
    assertEquals(3, s.getAgent().getVision());
  }

  @Test
  void getNextGrid_sugarGrowBackRate_respectsInterval() {
    // interval=2 → no grow on tick=0? Actually tick starts at 0 and 0%2==0, so grows first tick
    // interval=3 → 0%3==0 on first call, then 1%3≠0 (no grow), 2%3≠0, 3%3==0 (grow again)
    SugarScapeRule intervalRule = new SugarScapeRule(Map.of(
        "sugarGrowBackRate", 2.0,
        "sugarGrowBackInterval", 3.0));
    Patch p = new Patch(4); p.giveSugar();  // sugar=0
    Grid grid = makeGrid(new SugarScapeCellState[][]{{new SugarScapeCellState(p, null)}});

    Grid next1 = intervalRule.getNextGrid(grid);  // tick=0 (0%3==0) → grows by 2
    // tick=0 triggers grow → sugar=2
    assertEquals(2, state(next1, 0, 0).getPatchSugar());
  }
}
