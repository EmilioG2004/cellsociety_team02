package cellsociety.Model.CellState.SugarScape;

/**
 * A sugar patch on the SugarScape grid. Each patch has a sugar level and a maximum capacity.
 * Sugar can be harvested by agents and grows back over time.
 *
 * @author Michael Lei
 */
public class Patch {

  public static final int DEFAULT_SUGAR_CAPACITY = 4;
  public static final int DEFAULT_GROW_BACK_RATE = 1;
  public static final int DEFAULT_GROW_BACK_INTERVAL = 1;

  private int sugar;
  private final int sugarCapacity;

  /** Creates a patch with the given sugar capacity, initially full. */
  public Patch(int sugarCapacity) {
    this.sugarCapacity = sugarCapacity;
    this.sugar = sugarCapacity;
  }

  /** Creates a patch with the default sugar capacity. */
  public Patch() {
    this(DEFAULT_SUGAR_CAPACITY);
  }

  /** Creates a patch with the given capacity and current sugar level (for copying). */
  public Patch(int sugarCapacity, int currentSugar) {
    this.sugarCapacity = sugarCapacity;
    this.sugar = Math.min(sugarCapacity, Math.max(0, currentSugar));
  }

  /** Returns a copy of this patch with the same capacity and sugar level. */
  public Patch copy() {
    return new Patch(sugarCapacity, sugar);
  }

  /** Returns the current sugar level of this patch. */
  public int getSugar() {
    return this.sugar;
  }

  /** Harvests all sugar from this patch, resetting it to 0 and returning the amount taken. */
  public int giveSugar() {
    int amount = this.sugar;
    this.sugar = 0;
    return amount;
  }

  /** Adds growBackAmount to sugar, capping at sugarCapacity. */
  public void growBack(int growBackAmount) {
    this.sugar = Math.min(sugarCapacity, this.sugar + growBackAmount);
  }
}
