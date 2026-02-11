package cellsociety.config;

import java.util.Map;

public final class SimulationConfig {

  private final String simulationType;

  // metadata
  private final String title;
  private final String author;
  private final String description;

  // grid
  private final int rows;
  private final int cols;
  private final int[][] initialStates;

  // parameters
  private final Map<String, Double> parameters;

  public SimulationConfig(
      String simulationType,
      String title,
      String author,
      String description,
      int rows,
      int cols,
      int[][] initialStates,
      Map<String, Double> parameters
  ) {
    this.simulationType = simulationType;
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
