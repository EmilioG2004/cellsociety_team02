package cellsociety.Controller;

import cellsociety.Model.Simulation;
import cellsociety.Model.SimulationDriver;
import cellsociety.Model.SimulationFactory;
import cellsociety.View.SimulationView;
import cellsociety.config.ConfigLoader;
import cellsociety.config.SimulationConfig;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class SimulationController {


  private final SimulationView mySimView;
  private final SimulationDriver myDriver;
  private final ConfigLoader myConfigLoader;
  private final static double cellSize = 20.0;

  public SimulationController(SimulationView simView){
    this.mySimView = simView;
    this.myDriver = new SimulationDriver();
    this.myConfigLoader = new ConfigLoader();
    wireButtons();

    myDriver.setOnStepCallback(() -> Platform.runLater(() -> {
      Simulation mySim = myDriver.getSimulation();
      mySimView.getMyGridView().render(mySim.getGrid(), mySim.getRule().getStateColors());
    })
    );
  }

  public void setSimulation(Simulation sim) {
    myDriver.setSimulation(sim);
    mySimView.getMyGridView().render(sim.getGrid(), sim.getRule().getStateColors());
  }
 
  private void wireButtons(){
    wireStep();
    wireStart();
    wirePause();
    wireSave();
    wireUpload();
    wireSpeedUp();
    wireSlowDown();
  }
  private void wireStep(){
    mySimView.getMyStepButton().setOnAction(e -> {
      System.out.println("Stepping");
      myDriver.step();
    });
  }
  private void wireStart(){
    mySimView.getMyStartButton().setOnAction(e -> {
      System.out.println("Start pressed");
      myDriver.start();
    });
  }

  private void wirePause(){
    mySimView.getMyPauseButton().setOnAction(e -> {
      System.out.println("Pause pressed");
      myDriver.pause();
    });
  }

  private void wireSpeedUp(){
    mySimView.getMySpeedUpButton().setOnAction(e -> {
      System.out.println("Speeding Up");
      myDriver.setSpeed(myDriver.getStepsPerSecond() * 2);
    });
  }

  private void wireSlowDown(){
    mySimView.getMySlowDownButton().setOnAction(e -> {
      System.out.println("Slowing Down");
      myDriver.setSpeed(myDriver.getStepsPerSecond() / 2.0);
    });
  }

  private void wireSave(){
    mySimView.getMySaveButton().setOnAction(e -> {System.out.println("Saving File");});
  }

  private void wireUpload(){
    mySimView.getMyUploadButton().setOnAction(e -> {
      System.out.println("Uploading File");
      File myFile = mySimView.showOpenConfigDialog();
      try {
        SimulationConfig myConfig = myConfigLoader.loadConfig(myFile);
        Simulation mySim = SimulationFactory.createSimulation(myConfig);
        myDriver.setSimulation(mySim);
        mySimView.setGrid(mySim.getGrid().getRows(), mySim.getGrid().getCols(), cellSize);
        mySimView.getMyGridView().render(mySim.getGrid(), mySim.getRule().getStateColors());
      } catch (ParserConfigurationException | SAXException | IOException ex) {
        throw new RuntimeException(ex);
      }
    });
  }
}
