package cellsociety.Controller;

import cellsociety.View.SimulationView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class SimulationController {

  // will hold simulation, simulationdriver as well once made

  private final SimulationView mySimView;
  private Timeline myAnimation;
  private double speedMultiplier = 1.0;
  private final double baseFPS = 1.0;

  public SimulationController(SimulationView simView){
    this.mySimView = simView;
    buildTimeline();
    wireButtons();
  }

  private void buildTimeline(){
    double tickSpeed = 1.0 / (baseFPS * speedMultiplier);

    myAnimation = new Timeline(new KeyFrame(Duration.seconds(tickSpeed), e -> onTick()));
    myAnimation.setCycleCount(Timeline.INDEFINITE);
  }

  private void wireButtons(){
    wireStart();
    wirePause();
    wireSave();
    wireUpload();
    wireSpeedUp();
    wireSlowDown();
  }

  private void wireStart(){
    mySimView.getMyStartButton().setOnAction(e -> {
      System.out.println("Start pressed");
      myAnimation.play();
    });
  }

  private void wirePause(){
    mySimView.getMyPauseButton().setOnAction(e -> {
      System.out.println("Pause pressed");
      myAnimation.pause();
    });
  }

  private void wireSpeedUp(){
    mySimView.getMySpeedUpButton().setOnAction(e -> {System.out.println("Speeding Up");});
  }

  private void wireSlowDown(){
    mySimView.getMySlowDownButton().setOnAction(e -> {System.out.println("Slowing Down");});
  }

  private void wireSave(){
    mySimView.getMySaveButton().setOnAction(e -> {System.out.println("Saving File");});
  }

  private void wireUpload(){
    mySimView.getMyUploadButton().setOnAction(e -> {System.out.println("Uploading File");});
  }

  private void onTick(){

    // need simulation, simulationdriver
    mySimView.render(mySimulation.getGrid());
  }

}
