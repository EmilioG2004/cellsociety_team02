package cellsociety.Model;

import cellsociety.Model.CellState.CellState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controls run state (start/pause), speed, and drives periodic stepping. Invoked by the controller
 * when the user presses Start, Pause, or changes speed. Notifies a callback after each step so
 * the view can refresh. Does not depend on JavaFX or file format.
 */
public class SimulationDriver {
  private Simulation simulation;
  private boolean isRunning;
  private boolean isForward = true;
  private double stepsPerSecond;
  private Runnable onStepCallback;

  private final ScheduledExecutorService scheduler;
  private ScheduledFuture<?> stepTask;

  private static final double DEFAULT_SPEED = 1.0;

  public SimulationDriver() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = new Thread(r, "SimulationDriver");
      t.setDaemon(true);
      return t;
    });
    this.stepsPerSecond = DEFAULT_SPEED;
  }

  /**
   * Sets the current simulation. If the driver is running, the next tick will use this simulation.
   */
  public void setSimulation(Simulation simulation) {
    this.simulation = simulation;
  }

  /**
   * Sets the simulation's grid to the given state and updates its undo stack. Use this instead of
   * setting cell state on the grid directly so stepBack() stays correct. No-op if no simulation set.
   */
  public void setState(CellState[][] state) {
    if (simulation != null) {
      simulation.setState(state);
    }
  }

  /**
   * Returns the current simulation, or null if none set.
   */
  public Simulation getSimulation() {
    return simulation;
  }

  /**
   * Registers a callback invoked after each step. The view can use this to refresh the display.
   * May be called from the driver's thread; the view should use Platform.runLater if updating UI.
   */
  public void setOnStepCallback(Runnable onStepCallback) {
    this.onStepCallback = onStepCallback;
  }

  /**
   * Returns true if the driver is currently running (stepping periodically).
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * Returns the current stepping rate in steps per second.
   */
  public double getStepsPerSecond() {
    return stepsPerSecond;
  }

  /**
   * Starts periodic stepping at the current speed. No-op if already running or no simulation set.
   */
  public void start() {
    if (isRunning || simulation == null) {
      return;
    }
    isRunning = true;
    scheduleNextStep();
  }

  /**
   * Stops periodic stepping. Single-step via {@link #step()} still works when paused.
   */
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

  /**
   * Sets the stepping rate in steps per second. If running, reschedules at the new rate immediately.
   */
  public void setSpeed(double stepsPerSecond) {
    if (stepsPerSecond <= 0) {
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

  /**
   * Advances the current simulation by one generation and notifies the step callback. Use when
   * paused for single-step. No-op if no simulation set.
   */
  public void step() {
    if (simulation == null) {
      return;
    }
    simulation.step();
    if (onStepCallback != null) {
      onStepCallback.run();
    }
  }
  public void setDirection(boolean forward) {
    if (forward == isForward) {
      return;
    }
    this.isForward = forward;
    
  }
  public boolean isForwardDirection() {
    return isForward;
  }
  /**
   * Goes back one generation and notifies the step callback. Use when paused for single-step back. No-op if no simulation set.
   */
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
    /**
   * Schedules steps to occur at the current speed.
   */
  private void scheduleNextStep() {
    if (!isRunning || simulation == null) {
      return;
    }
    long periodNanos = (long) (1_000_000_000.0 / stepsPerSecond);
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
