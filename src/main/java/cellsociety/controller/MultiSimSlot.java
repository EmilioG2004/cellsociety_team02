package cellsociety.controller;

import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationFactory;
import cellsociety.config.SimulationConfig;
import cellsociety.view.SimulationPanel;
import javafx.scene.Node;

/**
 * Small container object representing one dashboard "slot":
 * a render-only panel + a presenter/driver that updates it.
 *
 * Purpose:
 * Makes it easy for MultiSimController to manage N panels uniformly.
 *
 * @author Emilio Garcia
 */
public class MultiSimSlot {

  private final int myIndex;
  private final SimulationPanel myPanel;
  private final SimulationPresenter myPresenter;
  private SimulationConfig myConfig;

  public MultiSimSlot(int index, SimulationPanel panel, SimulationPresenter presenter) {
    myIndex = index;
    myPanel = panel;
    myPresenter = presenter;
  }

  public int getIndex() {
    return myIndex;
  }

  public Node getNode() {
    return myPanel.getNode();
  }

  public SimulationPresenter getPresenter() {
    return myPresenter;
  }

  public void setSimulation(Simulation sim) {
    myPresenter.setSimulation(sim, sim.getReadOnlyGrid().getRows(), sim.getReadOnlyGrid().getCols());
  }

  public void setConfig(SimulationConfig config) {
    myConfig = config;
  }

  public void reset() {
    if (myConfig == null) return;
    Simulation sim = SimulationFactory.createSimulation(myConfig);
    setSimulation(sim);
  }

  public void toggleOutline() {
    if (!hasSimulation()) return;
    myPanel.toggleOutline(
        myPresenter.getDriver().getSimulation().getReadOnlyGrid(),
        myPresenter.getDriver().getSimulation().getRule().getStateColors()
    );
  }

  public void start() {
    myPresenter.getDriver().start();
  }

  public void pause() {
    myPresenter.getDriver().pause();
  }

  public void step() {
    myPresenter.getDriver().step();
  }

  public boolean stepBack() {
    return myPresenter.getDriver().stepBack();
  }

  public boolean hasSimulation() {
    return myPresenter.getDriver().hasSimulation();
  }

  public boolean isForwardDirection() {
    return myPresenter.getDriver().isForwardDirection();
  }

  public void setDirection(boolean forward) {
    myPresenter.getDriver().setDirection(forward);
  }

  public void setSpeed(double stepsPerSecond) {
    myPresenter.getDriver().setSpeed(stepsPerSecond);
  }

  public double getStepsPerSecond() {
    return myPresenter.getDriver().getStepsPerSecond();
  }
}
