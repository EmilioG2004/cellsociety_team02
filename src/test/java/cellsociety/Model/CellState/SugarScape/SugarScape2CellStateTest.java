package cellsociety.Model.CellState.SugarScape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.CellState;
import org.junit.jupiter.api.Test;

class SugarScape2CellStateTest {

  /** Creates a deterministic Agent2 via the child constructor (fixed max-age, fertile window). */
  private Agent2 fixedAgent2(int sugar, int metabolism, int vision) {
    return new Agent2(sugar, metabolism, vision, 100, 100, 12, 50);
  }

  // ---------- Z (Zero): vacant cell ----------

  @Test
  void toInt_noAgent_returnsPatchSugar() {
    SugarScape2CellState s = new SugarScape2CellState(new Patch(3), null);
    assertEquals(3, s.toInt());
  }

  @Test
  void toInt_zeroPatch_noAgent_returnsZero() {
    SugarScape2CellState s = new SugarScape2CellState(new Patch(0), null);
    assertEquals(0, s.toInt());
  }

  // ---------- O (One): single agent with gender ----------

  @Test
  void toInt_agent_returnsMaleOrFemaleInt() {
    Agent2 a = fixedAgent2(10, 1, 1);
    SugarScape2CellState s = new SugarScape2CellState(new Patch(), a);
    int result = s.toInt();
    assertTrue(
        result == SugarScape2CellState.MALE_AGENT_INT
            || result == SugarScape2CellState.FEMALE_AGENT_INT,
        "Non-post-fertile agent should return MALE or FEMALE int, got: " + result);
  }

  @Test
  void toInt_maleAgent_returnsNegativeOne() {
    // Find a male agent (may need a few tries due to random gender)
    Agent2 male = null;
    for (int i = 0; i < 100; i++) {
      Agent2 candidate = fixedAgent2(10, 1, 1);
      if (candidate.getGender() == Agent2.Gender.MALE) {
        male = candidate;
        break;
      }
    }
    assert male != null : "Could not create male agent in 100 tries";
    SugarScape2CellState s = new SugarScape2CellState(new Patch(), male);
    assertEquals(SugarScape2CellState.MALE_AGENT_INT, s.toInt());
  }

  @Test
  void toInt_femaleAgent_returnsNegativeTwo() {
    Agent2 female = null;
    for (int i = 0; i < 100; i++) {
      Agent2 candidate = fixedAgent2(10, 1, 1);
      if (candidate.getGender() == Agent2.Gender.FEMALE) {
        female = candidate;
        break;
      }
    }
    assert female != null : "Could not create female agent in 100 tries";
    SugarScape2CellState s = new SugarScape2CellState(new Patch(), female);
    assertEquals(SugarScape2CellState.FEMALE_AGENT_INT, s.toInt());
  }

  // ---------- B (Boundaries): post-fertile age ----------

  @Test
  void toInt_postFertileAgent_returnsNegativeThree() {
    Agent2 a = fixedAgent2(10, 1, 1);
    for (int i = 0; i < 51; i++) a.incrementAge();  // age=51 > fertileEnd=50
    SugarScape2CellState s = new SugarScape2CellState(new Patch(), a);
    assertEquals(SugarScape2CellState.POST_FERTILE_AGENT_INT, s.toInt());
  }

  // ---------- I (Interface): getNextInCycle creates Agent2 ----------

  @Test
  void getNextInCycle_fromVacant_createsAgent2Occupied() {
    SugarScape2CellState s = new SugarScape2CellState(new Patch(2), null);
    CellState next = s.getNextInCycle();
    assertInstanceOf(SugarScape2CellState.class, next);
    SugarScape2CellState next2 = (SugarScape2CellState) next;
    assertFalse(next2.isVacant());
    assertInstanceOf(Agent2.class, next2.getAgent());
  }

  @Test
  void getNextInCycle_fromOccupied_createsVacant() {
    SugarScape2CellState s = new SugarScape2CellState(new Patch(), fixedAgent2(10, 1, 1));
    CellState next = s.getNextInCycle();
    assertInstanceOf(SugarScape2CellState.class, next);
    assertTrue(((SugarScape2CellState) next).isVacant());
  }
}
