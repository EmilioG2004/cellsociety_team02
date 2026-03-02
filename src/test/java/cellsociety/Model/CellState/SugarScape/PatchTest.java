package cellsociety.Model.CellState.SugarScape;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PatchTest {

  // ---------- Z (Zero / empty): no sugar ----------

  @Test
  void giveSugar_emptyPatch_returnsZero() {
    Patch p = new Patch(0);
    assertEquals(0, p.giveSugar());
  }

  @Test
  void getSugar_emptyCapacity_isZero() {
    Patch p = new Patch(0);
    assertEquals(0, p.getSugar());
  }

  @Test
  void growBack_emptyCapacity_staysZero() {
    Patch p = new Patch(0);
    p.growBack(5);
    assertEquals(0, p.getSugar());
  }

  // ---------- O (One): single unit ----------

  @Test
  void defaultConstructor_sugarEqualsDefaultCapacity() {
    Patch p = new Patch();
    assertEquals(Patch.DEFAULT_SUGAR_CAPACITY, p.getSugar());
  }

  @Test
  void paramConstructor_sugarEqualsCapacity() {
    Patch p = new Patch(6);
    assertEquals(6, p.getSugar());
  }

  @Test
  void giveSugar_returnsCurrentSugar() {
    Patch p = new Patch(3);
    assertEquals(3, p.giveSugar());
  }

  @Test
  void giveSugar_resetsToZero() {
    Patch p = new Patch(3);
    p.giveSugar();
    assertEquals(0, p.getSugar());
  }

  // ---------- M (Many): grow-back logic ----------

  @Test
  void growBack_addsSugar() {
    Patch p = new Patch(4);
    p.giveSugar();  // sugar -> 0
    p.growBack(2);
    assertEquals(2, p.getSugar());
  }

  @Test
  void growBack_doesNotExceedCapacity() {
    Patch p = new Patch(4);
    p.giveSugar();  // sugar -> 0
    p.growBack(10);
    assertEquals(4, p.getSugar());
  }

  @Test
  void growBack_fullPatch_staysFull() {
    Patch p = new Patch(4);  // starts full
    p.growBack(1);
    assertEquals(4, p.getSugar());
  }

  @Test
  void growBack_zeroRate_noChange() {
    Patch p = new Patch(4);
    p.giveSugar();
    p.growBack(0);
    assertEquals(0, p.getSugar());
  }

  @Test
  void giveSugar_twice_secondCallReturnsZero() {
    Patch p = new Patch(3);
    p.giveSugar();
    assertEquals(0, p.giveSugar());
  }

  @Test
  void growBack_partialRefill_capped() {
    Patch p = new Patch(4);
    p.giveSugar();       // sugar=0
    p.growBack(3);       // sugar=3
    p.growBack(3);       // would be 6, capped at 4
    assertEquals(4, p.getSugar());
  }
}
