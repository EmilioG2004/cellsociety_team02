package cellsociety.Model.CellState.SugarScape;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.Model.CellState.CellState;
import org.junit.jupiter.api.Test;

class SugarScapeCellStateTest {

  private Agent fixedAgent() {
    return new Agent(5, 5, 1, 1, 1, 1);
  }

  // ---------- Z (Zero): vacant cell ----------

  @Test
  void isVacant_noAgent_returnsTrue() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), null);
    assertTrue(s.isVacant());
  }

  @Test
  void getAgent_noAgent_returnsNull() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), null);
    assertNull(s.getAgent());
  }

  @Test
  void toInt_noAgent_returnsPatchSugar() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(3), null);
    assertEquals(3, s.toInt());
  }

  // ---------- O (One): single agent ----------

  @Test
  void isVacant_withAgent_returnsFalse() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), fixedAgent());
    assertFalse(s.isVacant());
  }

  @Test
  void getAgent_returnsAgent() {
    Agent a = fixedAgent();
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), a);
    assertSame(a, s.getAgent());
  }

  @Test
  void toInt_withAgent_returns100() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), fixedAgent());
    assertEquals(100, s.toInt());
  }

  // ---------- M (Many): state transitions ----------

  @Test
  void getPatch_returnsConstructedPatch() {
    Patch p = new Patch(3);
    SugarScapeCellState s = new SugarScapeCellState(p, null);
    assertSame(p, s.getPatch());
  }

  @Test
  void getPatchSugar_returnsPatchSugar() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(3), null);
    assertEquals(3, s.getPatchSugar());
  }

  @Test
  void setAgent_updatesAgent() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), null);
    Agent a = fixedAgent();
    s.setAgent(a);
    assertSame(a, s.getAgent());
    assertFalse(s.isVacant());
  }

  @Test
  void setAgent_null_makesVacant() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), fixedAgent());
    s.setAgent(null);
    assertTrue(s.isVacant());
    assertNull(s.getAgent());
  }

  // ---------- B (Boundaries): zero-sugar patch ----------

  @Test
  void toInt_zeroPatch_returnsZero() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(0), null);
    assertEquals(0, s.toInt());
  }

  // ---------- I (Interface): getNextInCycle cycles occupied <-> vacant ----------

  @Test
  void getNextInCycle_fromVacant_createsOccupied() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), null);
    CellState next = s.getNextInCycle();
    assertInstanceOf(SugarScapeCellState.class, next);
    assertFalse(((SugarScapeCellState) next).isVacant());
  }

  @Test
  void getNextInCycle_fromOccupied_createsVacant() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), fixedAgent());
    CellState next = s.getNextInCycle();
    assertInstanceOf(SugarScapeCellState.class, next);
    assertTrue(((SugarScapeCellState) next).isVacant());
  }

  @Test
  void getNextInCycle_returnsNewInstance() {
    SugarScapeCellState s = new SugarScapeCellState(new Patch(), null);
    assertNotNull(s.getNextInCycle());
  }
}
