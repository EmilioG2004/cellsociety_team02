package cellsociety.Model.CellState.SugarScape;

/**
 * Extended agent for SugarScape reproduction preset. Adds gender, age, maxAge,
 * fertility window, and tracks initial sugar endowment for reproduction costs.
 *
 * @author Michael Lei
 */
public class Agent2 extends Agent {

  public static final int DEFAULT_MAX_AGE_MIN = 60;
  public static final int DEFAULT_MAX_AGE_MAX = 100;
  public static final int DEFAULT_FERTILE_START = 12;
  public static final int DEFAULT_FERTILE_END = 50;

  public enum Gender { MALE, FEMALE }

  private final Gender gender;
  private int age;
  private final int maxAge;
  private final int fertileStart;
  private final int fertileEnd;
  private final int initialSugar;

  /**
   * Creates a new agent with randomized traits within the given limits.
   * Used during grid initialization.
   */
  public Agent2(Integer initSugarMin, Integer initSugarMax,
                Integer metabolismMin, Integer metabolismMax,
                Integer visionMin, Integer visionMax,
                Integer maxAgeMin, Integer maxAgeMax,
                Integer fertileStartParam, Integer fertileEndParam) {
    super(initSugarMin, initSugarMax, metabolismMin, metabolismMax, visionMin, visionMax);
    this.gender = randomInclusive(0, 1) == 0 ? Gender.MALE : Gender.FEMALE;
    this.age = 0;
    this.maxAge = randomInclusive(
        maxAgeMin != null ? maxAgeMin : DEFAULT_MAX_AGE_MIN,
        maxAgeMax != null ? maxAgeMax : DEFAULT_MAX_AGE_MAX);
    this.fertileStart = fertileStartParam != null ? fertileStartParam : DEFAULT_FERTILE_START;
    this.fertileEnd = fertileEndParam != null ? fertileEndParam : DEFAULT_FERTILE_END;
    this.initialSugar = getSugar();
  }

  /**
   * Creates a child agent with specific inherited values from parents.
   * Vision and metabolism are exact (inherited from one parent each).
   */
  public Agent2(int sugar, int metabolism, int vision,
                Integer maxAgeMin, Integer maxAgeMax,
                Integer fertileStartParam, Integer fertileEndParam) {
    super(sugar, sugar, metabolism, metabolism, vision, vision);
    this.gender = randomInclusive(0, 1) == 0 ? Gender.MALE : Gender.FEMALE;
    this.age = 0;
    this.maxAge = randomInclusive(
        maxAgeMin != null ? maxAgeMin : DEFAULT_MAX_AGE_MIN,
        maxAgeMax != null ? maxAgeMax : DEFAULT_MAX_AGE_MAX);
    this.fertileStart = fertileStartParam != null ? fertileStartParam : DEFAULT_FERTILE_START;
    this.fertileEnd = fertileEndParam != null ? fertileEndParam : DEFAULT_FERTILE_END;
    this.initialSugar = sugar;
  }

  public Gender getGender() {
    return gender;
  }

  public int getAge() {
    return age;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public int getInitialSugar() {
    return initialSugar;
  }

  public void incrementAge() {
    age++;
  }

  /** Returns true if the agent has not exceeded its maximum age. */
  public boolean isAlive() {
    return age < maxAge;
  }

  /**
   * Returns true if the agent is within its fertility window and has at least
   * as much sugar as its initial endowment.
   */
  public boolean isFertile() {
    return age >= fertileStart && age <= fertileEnd && getSugar() >= initialSugar;
  }

  /** Returns true if the agent's age is above the maximum fertility limit. */
  public boolean isPastFertileAge() {
    return age > fertileEnd;
  }
}
