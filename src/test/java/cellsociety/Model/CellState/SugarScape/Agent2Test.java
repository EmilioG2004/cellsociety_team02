package cellsociety.Model.CellState.SugarScape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Agent2Test {

  /**
   * Creates a deterministic child-constructor Agent2 with fixed maxAge, fertileStart, fertileEnd.
   */
  private Agent2 fixedAgent(int sugar, int metabolism, int vision,
      int maxAge, int fertileStart, int fertileEnd) {
    return new Agent2(sugar, metabolism, vision, maxAge, maxAge, fertileStart, fertileEnd);
  }

  // ---------- Z (Zero): newly created (age=0) ----------

  @Test
  void newAgent_ageIsZero() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    assertEquals(0, a.getAge());
  }

  @Test
  void newAgent_isAlive() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    assertTrue(a.isAlive());
  }

  @Test
  void newAgent_tooYoungToBeInFertileWindow_isNotFertile() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    assertFalse(a.isFertile());
  }

  // ---------- O (One): single increment ----------

  @Test
  void incrementAge_incrementsByOne() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    a.incrementAge();
    assertEquals(1, a.getAge());
  }

  @Test
  void initialSugar_storedFromConstruction() {
    Agent2 a = fixedAgent(15, 1, 1, 100, 12, 50);
    assertEquals(15, a.getInitialSugar());
  }

  @Test
  void gender_isAssigned() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    assertNotNull(a.getGender());
    assertInstanceOf(Agent2.Gender.class, a.getGender());
  }

  @Test
  void maxAge_setFromParams() {
    Agent2 a = new Agent2(10, 10, 1, 1, 1, 1, 80, 80, 12, 50);
    assertEquals(80, a.getMaxAge());
  }

  // ---------- M (Many): aging over multiple ticks ----------

  @Test
  void isAlive_beforeMaxAge_returnsTrue() {
    Agent2 a = fixedAgent(10, 1, 1, 5, 12, 50);
    for (int i = 0; i < 4; i++) a.incrementAge();  // age=4, maxAge=5
    assertTrue(a.isAlive());
  }

  @Test
  void isAlive_atMaxAge_returnsFalse() {
    Agent2 a = fixedAgent(10, 1, 1, 1, 12, 50);
    a.incrementAge();  // age=1 >= maxAge=1
    assertFalse(a.isAlive());
  }

  @Test
  void isFertile_atFertileStart_withEnoughSugar_returnsTrue() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    for (int i = 0; i < 12; i++) a.incrementAge();  // age=12 == fertileStart
    assertTrue(a.isFertile());
  }

  @Test
  void isFertile_atFertileEnd_returnsTrue() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    for (int i = 0; i < 50; i++) a.incrementAge();  // age=50 == fertileEnd
    assertTrue(a.isFertile());
  }

  @Test
  void isFertile_beyondFertileEnd_returnsFalse() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    for (int i = 0; i < 51; i++) a.incrementAge();  // age=51 > fertileEnd=50
    assertFalse(a.isFertile());
  }

  @Test
  void isFertile_notEnoughSugar_returnsFalse() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    for (int i = 0; i < 12; i++) a.incrementAge();  // age=12 (fertile age)
    a.gainSugar(-5);  // sugar=5 < initialSugar=10
    assertFalse(a.isFertile());
  }

  // ---------- B (Boundaries): fertility window edges ----------

  @Test
  void isPastFertileAge_atFertileEnd_returnsFalse() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    for (int i = 0; i < 50; i++) a.incrementAge();  // age=50 == fertileEnd
    assertFalse(a.isPastFertileAge());
  }

  @Test
  void isPastFertileAge_oneBeyondFertileEnd_returnsTrue() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    for (int i = 0; i < 51; i++) a.incrementAge();  // age=51 > fertileEnd=50
    assertTrue(a.isPastFertileAge());
  }

  // ---------- I (Interface): inherits Agent methods ----------

  @Test
  void agent2_inheritsMetabolize() {
    Agent2 a = fixedAgent(3, 1, 1, 100, 12, 50);
    assertTrue(a.metabolize());  // sugar=2
    assertTrue(a.metabolize());  // sugar=1
    assertFalse(a.metabolize()); // sugar=0
  }

  @Test
  void agent2_inheritsGainSugar() {
    Agent2 a = fixedAgent(10, 1, 1, 100, 12, 50);
    a.gainSugar(5);
    assertEquals(15, a.getSugar());
  }

  // ---------- E (Exceptional): null params use defaults ----------

  @Test
  void nullMaxAge_usesDefaultRange() {
    Agent2 a = new Agent2(10, 10, 1, 1, 1, 1, null, null, null, null);
    assertTrue(a.getMaxAge() >= Agent2.DEFAULT_MAX_AGE_MIN);
    assertTrue(a.getMaxAge() <= Agent2.DEFAULT_MAX_AGE_MAX);
  }
}
