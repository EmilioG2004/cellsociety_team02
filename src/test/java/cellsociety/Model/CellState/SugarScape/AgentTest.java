package cellsociety.Model.CellState.SugarScape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AgentTest {

  /** Creates an agent with all traits fixed to a single value (min == max). */
  private Agent fixedAgent(int sugar, int metabolism, int vision) {
    return new Agent(sugar, sugar, metabolism, metabolism, vision, vision);
  }

  // ---------- Z (Zero): edge sugar values ----------

  @Test
  void metabolize_sugarEqualsMetabolism_returnsFalse() {
    Agent a = fixedAgent(1, 1, 1);
    assertFalse(a.metabolize());
    assertEquals(0, a.getSugar());
  }

  @Test
  void metabolize_sugarLessThanMetabolism_returnsFalse() {
    Agent a = fixedAgent(1, 2, 1);
    assertFalse(a.metabolize());
    assertEquals(-1, a.getSugar());
  }

  // ---------- O (One): single step / single agent ----------

  @Test
  void defaultConstructor_valuesInDefaultRange() {
    Agent a = new Agent();
    assertTrue(a.getSugar() >= Agent.DEFAULT_INIT_SUGAR_MIN);
    assertTrue(a.getSugar() <= Agent.DEFAULT_INIT_SUGAR_MAX);
    assertTrue(a.getSugarMetabolism() >= Agent.DEFAULT_SUGAR_METABOLISM_MIN);
    assertTrue(a.getSugarMetabolism() <= Agent.DEFAULT_SUGAR_METABOLISM_MAX);
    assertTrue(a.getVision() >= Agent.DEFAULT_VISION_MIN);
    assertTrue(a.getVision() <= Agent.DEFAULT_VISION_MAX);
  }

  @Test
  void paramConstructor_fixedValues_setCorrectly() {
    Agent a = fixedAgent(10, 2, 3);
    assertEquals(10, a.getSugar());
    assertEquals(2, a.getSugarMetabolism());
    assertEquals(3, a.getVision());
  }

  @Test
  void nullParams_useDefaults() {
    Agent a = new Agent(null, null, null, null, null, null);
    assertTrue(a.getSugar() >= Agent.DEFAULT_INIT_SUGAR_MIN);
    assertTrue(a.getSugar() <= Agent.DEFAULT_INIT_SUGAR_MAX);
  }

  // ---------- M (Many): multiple mutations ----------

  @Test
  void gainSugar_positive_increasesSugar() {
    Agent a = fixedAgent(10, 1, 1);
    a.gainSugar(5);
    assertEquals(15, a.getSugar());
  }

  @Test
  void gainSugar_negative_decreasesSugar() {
    Agent a = fixedAgent(10, 1, 1);
    a.gainSugar(-3);
    assertEquals(7, a.getSugar());
  }

  @Test
  void gainSugar_zero_noChange() {
    Agent a = fixedAgent(10, 1, 1);
    a.gainSugar(0);
    assertEquals(10, a.getSugar());
  }

  @Test
  void metabolize_highSugar_returnsTrue() {
    Agent a = fixedAgent(5, 1, 1);
    assertTrue(a.metabolize());
    assertEquals(4, a.getSugar());
  }

  @Test
  void metabolize_calledRepeatedly_decreasesEachTime() {
    Agent a = fixedAgent(3, 1, 1);
    assertTrue(a.metabolize());   // sugar=2
    assertTrue(a.metabolize());   // sugar=1
    assertFalse(a.metabolize());  // sugar=0
    assertEquals(0, a.getSugar());
  }

  // ---------- B (Boundaries): min/max limits reversed ----------

  @Test
  void paramConstructor_swappedMinMax_stillRandomizesCorrectly() {
    // Agent swaps low/high if inverted
    Agent a = new Agent(10, 5, 2, 1, 4, 2);
    assertTrue(a.getSugar() >= 5 && a.getSugar() <= 10);
    assertTrue(a.getSugarMetabolism() >= 1 && a.getSugarMetabolism() <= 2);
    assertTrue(a.getVision() >= 2 && a.getVision() <= 4);
  }
}
