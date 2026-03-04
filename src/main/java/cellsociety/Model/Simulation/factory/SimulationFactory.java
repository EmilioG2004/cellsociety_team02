package cellsociety.Model.Simulation.factory;

import cellsociety.Model.CellState.CellState;
import cellsociety.Model.GameRules.CellFactory;
import cellsociety.Model.GameRules.GameRule;
import cellsociety.Model.Grid.ArrayGrid;
import cellsociety.Model.Grid.Grid;
import cellsociety.Model.GridEdge.GridEdge;
import cellsociety.Model.NeighborStrategy.NeighborStrategy;
import cellsociety.Model.Simulation.model.Simulation;
import cellsociety.Model.Simulation.model.SimulationModel;
import cellsociety.config.SimulationConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Builds a {@link Simulation} from {@link SimulationConfig} using reflection for rule and
 * neighbor strategy classes.
 *
 * @author Michael Lei
 */
public class SimulationFactory {

  private static final String ERROR_MESSAGE_INVALID_CONFIG = "Invalid simulation configuration: ";
  private static final String ERROR_RULE_CLASS_BLANK = "Rule class name is blank";

  /**
   * Creates a simulation from a configuration.
   *
   * @param config simulation config; must not be null
   * @return a new {@link SimulationModel} with the configured grid and rule
   * @throws IllegalArgumentException if config is null, or rule/grid/state data is invalid
   */
  public static SimulationModel createSimulation(SimulationConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    try {
      String ruleClassName = config.getRuleClass();
      Map<String, Double> params =
          config.getParameters() != null ? config.getParameters() : Map.of();
      GameRule rule = createRuleFromClass(ruleClassName, params);
      Grid grid = createGrid(config, rule, rule.getDefaultNeighborStrategy());
      return new Simulation(grid, rule);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_CONFIG + e.getMessage(), e);
    }
  }

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

  private static GameRule createRuleFromClass(String ruleClassName, Map<String, Double> params) {
    if (ruleClassName == null || ruleClassName.isBlank()) {
      throw new IllegalArgumentException(ERROR_RULE_CLASS_BLANK);
    }
    String trimmed = ruleClassName.trim();
    try {
      Class<?> clazz = Class.forName(trimmed);
      if (!GameRule.class.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Rule class must extend GameRule: " + trimmed);
      }
      try {
        Constructor<?> constructor = clazz.getConstructor(Map.class);
        return (GameRule) constructor.newInstance(params);
      } catch (NoSuchMethodException e) {
        Constructor<?> noArg = clazz.getConstructor();
        return (GameRule) noArg.newInstance();
      }
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Rule class not found: " + trimmed, e);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "Rule class must have a public constructor (Map<String, Double>) or (): " + trimmed, e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      String msg = cause != null ? cause.getMessage() : e.getMessage();
      throw new IllegalArgumentException("Rule constructor failed: " + msg, e);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Failed to instantiate rule: " + e.getMessage(), e);
    }
  }

  private static Grid createGrid(SimulationConfig config, CellFactory cellFactory, NeighborStrategy defaultStrategy) {
    int rows = config.getRows();
    int cols = config.getCols();
    NeighborStrategy strategy = (config.getNeighborStrategyClass() != null && !config.getNeighborStrategyClass().isBlank())
        ? createNeighborStrategyFromClass(config.getNeighborStrategyClass())
        : defaultStrategy;
    String gridEdgeClass = config.getGridEdgeClass();
    if (gridEdgeClass != null && !gridEdgeClass.isBlank()) {
      strategy.setGridEdge(createGridEdgeFromClass(gridEdgeClass));
    }
    int[][] intStates = config.getInitialStates();
    CellState[][] states = new CellState[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        states[r][c] = cellFactory.fromInt(intStates[r][c]);
      }
    }
    return new ArrayGrid(states, strategy);
  }

  private static GridEdge createGridEdgeFromClass(String gridEdgeClassName) {
    String trimmed = gridEdgeClassName.trim();
    try {
      Class<?> clazz = Class.forName(trimmed);
      if (!GridEdge.class.isAssignableFrom(clazz)) {
        throw new IllegalArgumentException("Class must implement GridEdge: " + trimmed);
      }
      return (GridEdge) clazz.getConstructor().newInstance();
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("GridEdge class not found: " + trimmed, e);
    } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("GridEdge class must have a public no-arg constructor: " + trimmed, e);
    } catch (ReflectiveOperationException e) {
      Throwable cause = e.getCause();
      String msg = cause != null ? cause.getMessage() : e.getMessage();
      throw new IllegalArgumentException("GridEdge instantiation failed: " + msg, e);
    }
  }
}
