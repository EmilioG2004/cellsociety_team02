package cellsociety.config;

import java.util.Map;

/**
 * Immutable snapshot of a simulation configuration: rule class, metadata, grid size, initial states, and parameters.
 * Typically produced by {@link ConfigLoader} and consumed by {@link cellsociety.Model.Simulation.SimulationFactory}.
 */
public final class SimulationConfig {

  private final String simulationType;
  private final String ruleClass;

  private final String title;
  private final String author;
  private final String description;

  private final int rows;
  private final int cols;
  private final int[][] initialStates;

  private final Map<String, Double> parameters;

  /**
   * Constructs a simulation config.
   *
   * @param simulationType display name for the simulation (e.g. "GameOfLife")
   * @param ruleClass      fully qualified class name of the rule (e.g. "cellsociety.Model.GameRules.ConwayGameOfLifeRule")
   * @param title          simulation title
   * @param author         author
   * @param description    description
   * @param rows           number of grid rows
   * @param cols           number of grid columns
   * @param initialStates  initial cell state matrix (rows x cols)
   * @param parameters     optional parameters for the rule (not null; may be empty)
   */
  public SimulationConfig(
      String simulationType,
      String ruleClass,
      String title,
      String author,
      String description,
      int rows,
      int cols,
      int[][] initialStates,
      Map<String, Double> parameters
  ) {
    this.simulationType = simulationType;
    this.ruleClass = ruleClass;
    this.title = title;
    this.author = author;
    this.description = description;
    this.rows = rows;
    this.cols = cols;
    this.initialStates = initialStates;
    this.parameters = Map.copyOf(parameters);
  }

  // --- getters only (immutability) ---

  public String getSimulationType() {
    return simulationType;
  }
  public String getRuleClass()
  {
    return ruleClass;
  }
  public String getTitle() {
    return title;
  }
  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

  public int [][] getInitialStates() {
    return initialStates;
  }

  public Map<String, Double> getParameters() {
    return parameters;
  }
}
