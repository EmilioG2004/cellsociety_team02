package cellsociety.Model.Simulation.driver;

import cellsociety.Model.Grid.Grid;
import cellsociety.Model.Simulation.model.SimulationModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link SimulationDriver}: controls run state (start/pause), speed,
 * direction, and drives periodic stepping. Does not depend on JavaFX or file format.
 *
 * @author Michael Lei
 */
public class DefaultSimulationDriver implements SimulationDriver {

  private SimulationModel simulation;
  private boolean isRunning;
  private boolean isForward = true;
  private double stepsPerSecond;
  private Runnable onStepCallback;

  private final ScheduledExecutorService scheduler;
  private ScheduledFuture<?> stepTask;

  private static final double DEFAULT_SPEED = 1.0;
  private static final String THREAD_NAME = "SimulationDriver";
  private static final double SPEED_FLOOR = 0.0;
  private static final double NANOS_PER_SECOND = 1_000_000_000.0;
  private static final String ERROR_NO_SIMULATION = "No simulation set";

  public DefaultSimulationDriver() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, THREAD_NAME);
      t.setDaemon(true);
      return t;
    });
    this.stepsPerSecond = DEFAULT_SPEED;
  }

  @Override
  public void setSimulation(SimulationModel simulation) {
    this.simulation = simulation;
  }

  @Override
  public void setState(Grid state) {
    if (simulation != null) {
      simulation.setState(state);
    }
  }

  @Override
  public void toggleCellState(int row, int col) {
    if (simulation != null) {
      simulation.toggleCellState(row, col);
    }
  }

  @Override
  public boolean hasSimulation() {
    return simulation != null;
  }

  @Override
  public SimulationModel getSimulation() {
    if (simulation == null) {
      throw new IllegalStateException(ERROR_NO_SIMULATION);
    }
    return simulation;
  }

  @Override
  public void setOnStepCallback(Runnable onStepCallback) {
    this.onStepCallback = onStepCallback;
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public double getStepsPerSecond() {
    return stepsPerSecond;
  }

  @Override
  public void start() {
    if (isRunning || simulation == null) {
      return;
    }
    isRunning = true;
    scheduleNextStep();
  }

  @Override
  public void pause() {
    if (!isRunning) {
      return;
    }
    isRunning = false;
    if (stepTask != null) {
      stepTask.cancel(false);
      stepTask = null;
    }
  }

  @Override
  public void setSpeed(double stepsPerSecond) {
    if (stepsPerSecond <= SPEED_FLOOR) {
      stepsPerSecond = DEFAULT_SPEED;
    }
    this.stepsPerSecond = stepsPerSecond;
    if (isRunning) {
      if (stepTask != null) {
        stepTask.cancel(false);
        stepTask = null;
      }
      scheduleNextStep();
    }
  }

  @Override
  public void step() {
    if (simulation == null) {
      return;
    }
    simulation.step();
    if (onStepCallback != null) {
      onStepCallback.run();
    }
  }

  @Override
  public void setDirection(boolean forward) {
    if (forward == isForward) {
      return;
    }
    this.isForward = forward;
  }

  @Override
  public boolean isForwardDirection() {
    return isForward;
  }

  @Override
  public boolean stepBack() {
    if (simulation == null) {
      return false;
    }
    try {
      simulation.stepBack();
      if (onStepCallback != null) {
        onStepCallback.run();
      }
    } catch (IllegalStateException e) {
      return false;
    }
    return true;
  }

  private void scheduleNextStep() {
    if (!isRunning || simulation == null) {
      return;
    }
    long periodNanos = (long) (NANOS_PER_SECOND / stepsPerSecond);
    stepTask = scheduler.scheduleAtFixedRate(
        this::tick,
        periodNanos,
        periodNanos,
        TimeUnit.NANOSECONDS);
  }

  private void tick() {
    if (!isRunning) {
      return;
    }
    if (isForward) {
      step();
    } else {
      stepBack();
    }
  }
}
