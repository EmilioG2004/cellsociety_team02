package cellsociety.Model.Simulation;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Grid.MutableGrid;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.config.SimulationConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/** Builds a Simulation from a SimulationConfig (rule class, dimensions, initial states, parameters). */
public class SimulationFactory {

  private static final String ERROR_MESSAGE_INVALID_CONFIG = "Invalid simulation configuration: ";
  private static final String ERROR_RULE_CLASS_BLANK = "Rule class name is blank";

  /**
   * Instantiates a NeighborStrategy from its fully qualified class name using reflection.
   * The class must have a public no-arg constructor.
   *
   * @param strategyClassName fully qualified name of a class that implements NeighborStrategy
   * @return the constructed NeighborStrategy instance
   * @throws IllegalArgumentException if the class cannot be loaded, has no no-arg constructor,
   *                                   construction fails, or the class does not implement NeighborStrategy
   */
  private static NeighborStrategy createNeighborStrategyFromClass(String strategyClassName) {
    String trimmed = strategyClassName.trim();
    try {
      Class<?> clazz = Class.forName(trimmed);
      if (!NeighborStrategy.class.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Class must implement NeighborStrategy: " + trimmed);
      }
      Constructor<?> constructor = clazz.getConstructor();
      return (NeighborStrategy) constructor.newInstance();
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("NeighborStrategy class not found: " + trimmed, e);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "NeighborStrategy class must have a public no-arg constructor: " + trimmed, e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      String msg = cause != null ? cause.getMessage() : e.getMessage();
      throw new IllegalArgumentException("NeighborStrategy constructor failed: " + msg, e);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException(
          "Failed to instantiate NeighborStrategy: " + e.getMessage(), e);
    }
  }

  /**
   * Instantiates a GameRule from its fully qualified class name using reflection.
   * If {@code strategy} is non-null the rule's {@code (NeighborStrategy, Map)} constructor is used;
   * otherwise the {@code (Map)} constructor is used and the rule falls back to its own default.
   *
   * @param ruleClassName fully qualified name of a class that extends GameRule
   * @param params        parameters to pass to the rule constructor (may be empty)
   * @param strategy      neighbor strategy to inject, or null to use the rule's hardcoded default
   * @return the constructed GameRule instance
   * @throws IllegalArgumentException if the class cannot be loaded, has no suitable constructor,
   *                                   construction fails, or the class does not extend GameRule
   */
  private static GameRule createRuleFromClass(String ruleClassName, Map<String, Double> params,
      NeighborStrategy strategy) {
    if (ruleClassName == null || ruleClassName.isBlank()) {
      throw new IllegalArgumentException(ERROR_RULE_CLASS_BLANK);
    }
    String trimmed = ruleClassName.trim();
    try {
      Class<?> clazz = Class.forName(trimmed);
      if (!GameRule.class.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Rule class must extend GameRule: " + trimmed);
      }
      if (strategy != null) {
        Constructor<?> constructor = clazz.getConstructor(NeighborStrategy.class, Map.class);
        return (GameRule) constructor.newInstance(strategy, params);
      } else {
        Constructor<?> constructor = clazz.getConstructor(Map.class);
        return (GameRule) constructor.newInstance(params);
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Rule class not found: " + trimmed, e);
    } catch (NoSuchMethodException e) {
      String expected = strategy != null
          ? "(NeighborStrategy, Map<String, Double>)"
          : "(Map<String, Double>)";
      throw new IllegalArgumentException(
          "Rule class must have a public constructor " + expected + ": " + trimmed, e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      String msg = cause != null ? cause.getMessage() : e.getMessage();
      throw new IllegalArgumentException("Rule constructor failed: " + msg, e);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Failed to instantiate rule: " + e.getMessage(), e);
    }
  }
  /**
   * Builds a grid from config dimensions and initial states, using the rule to convert int states to cell states.
   *
   * @param config simulation config containing rows, cols, and initial state matrix
   * @param rule   game rule used to convert integer codes to {@link CellState} via {@link GameRule#fromInt(int)}
   * @return a mutable grid populated with the initial states
   */
  public static MutableGrid createGrid(SimulationConfig config, GameRule rule)
  {
    int rows = config.getRows();
    int cols = config.getCols();
    MutableGrid grid = new Grid(rows, cols);
    int[][] intStates = config.getInitialStates();
    CellState[][] cellStates = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cellStates[r][c] = rule.fromInt(intStates[r][c]);
      }
    }
    grid.setCellsState(cellStates);
    return grid;
  }
  /**
   * Creates a simulation from a configuration by loading the rule class via reflection and building the grid.
   *
   * @param config simulation config containing rule class name, dimensions, initial states, and parameters
   * @return a new {@link Simulation} with the configured grid and rule
   * @throws IllegalArgumentException if config is null, rule class cannot be loaded or instantiated,
   *                                   or grid/state data is invalid
   */
  public static Simulation createSimulation(SimulationConfig config) throws IllegalArgumentException {
    if (config == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    try {
      String ruleClassName = config.getRuleClass();
      Map<String, Double> params =
          config.getParameters() != null ? config.getParameters() : Map.of();
      String neighborStrategyClass = config.getNeighborStrategyClass();
      NeighborStrategy strategy = (neighborStrategyClass != null && !neighborStrategyClass.isBlank())
          ? createNeighborStrategyFromClass(neighborStrategyClass)
          : null;
      GameRule rule = createRuleFromClass(ruleClassName, params, strategy);
      MutableGrid grid = createGrid(config, rule);
      return new Simulation(grid, rule);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_CONFIG + e.getMessage(), e);
    }
  }
}
