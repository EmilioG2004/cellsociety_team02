package cellsociety.Model.CellState.SugarScape;

import java.util.Random;

/**
 * Automaton in the Sugarscape simulation. Each agent has sugar (initial endowment, changes over life),
 * sugarMetabolism and vision (genetic traits, fixed for life). Values are randomized between given
 * limits at creation; if limits are not provided, default limits are used.
 *
 * @author Michael Lei
 */
public class Agent {

  // --- Default limits (used when no limits are provided) ---
  public static final int DEFAULT_INIT_SUGAR_MIN = 5;
  public static final int DEFAULT_INIT_SUGAR_MAX = 25;
  public static final int DEFAULT_SUGAR_METABOLISM_MIN = 1;
  public static final int DEFAULT_SUGAR_METABOLISM_MAX = 4;
  public static final int DEFAULT_VISION_MIN = 1;
  public static final int DEFAULT_VISION_MAX = 6;

  private static final Random RNG = new Random();

  /** Current sugar endowment; changes over the agent's life. */
  private int sugar;
  /** Sugar consumed per step; fixed for the agent's life. */
  private final int sugarMetabolism;
  /** How far the agent can see; fixed for the agent's life. */
  private final int vision;

  /**
   * Creates an agent with sugar, sugarMetabolism, and vision randomized between
   * the default limits (inclusive).
   */
  public Agent() {
    this(null, null, null, null, null, null);
  }

  /**
   * Creates an agent with values randomized between the given limits (inclusive).
   * Any limit passed as null uses the corresponding default limit.
   *
   * @param initSugarMin  min for initial sugar (null = default)
   * @param initSugarMax  max for initial sugar (null = default)
   * @param metabolismMin min for sugarMetabolism (null = default)
   * @param metabolismMax max for sugarMetabolism (null = default)
   * @param visionMin     min for vision (null = default)
   * @param visionMax     max for vision (null = default)
   */
  public Agent(Integer initSugarMin, Integer initSugarMax,
               Integer metabolismMin, Integer metabolismMax,
               Integer visionMin, Integer visionMax) {
    int smin = initSugarMin != null ? initSugarMin : DEFAULT_INIT_SUGAR_MIN;
    int smax = initSugarMax != null ? initSugarMax : DEFAULT_INIT_SUGAR_MAX;
    int mmin = metabolismMin != null ? metabolismMin : DEFAULT_SUGAR_METABOLISM_MIN;
    int mmax = metabolismMax != null ? metabolismMax : DEFAULT_SUGAR_METABOLISM_MAX;
    int vmin = visionMin != null ? visionMin : DEFAULT_VISION_MIN;
    int vmax = visionMax != null ? visionMax : DEFAULT_VISION_MAX;

    this.sugar = randomInclusive(smin, smax);
    this.sugarMetabolism = randomInclusive(mmin, mmax);
    this.vision = randomInclusive(vmin, vmax);
  }

  /** Returns a random int in [low, high] inclusive. */
  protected static int randomInclusive(int low, int high) {
    if (low > high) {
      int t = low;
      low = high;
      high = t;
    }
    return low + RNG.nextInt(high - low + 1);
  }

  /** Adds the given amount of sugar to this agent's endowment (can be negative). */
  public void gainSugar(int sugar) {
    this.sugar += sugar;
  }

  /**
   * Subtracts this agent's metabolism from its sugar.
   *
   * @return true if the agent survives (sugar > 0), false if it dies
   */
  public boolean metabolize() {
    this.sugar -= this.sugarMetabolism;
    return this.sugar > 0;
  }

  /** Returns this agent's current sugar endowment. */
  public int getSugar() {
    return sugar;
  }

  /** Returns this agent's sugar metabolism rate (fixed for life). */
  public int getSugarMetabolism() {
    return sugarMetabolism;
  }

  /** Returns this agent's vision range (fixed for life). */
  public int getVision() {
    return vision;
  }
}
