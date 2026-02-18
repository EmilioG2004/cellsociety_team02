package cellsociety.controller;

import cellsociety.Model.Simulation;
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
}
