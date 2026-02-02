package cellsociety.Controller;

import cellsociety.View.SimulationView;

public class SimulationController {

  private final SimulationView mySimView;

  public SimulationController(SimulationView simView){
    this.mySimView = simView;
    wireButtons();
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
    mySimView.getMyStartButton().setOnAction(e -> {System.out.println("Start pressed");});
  }

  private void wirePause(){
    mySimView.getMyPauseButton().setOnAction(e -> {System.out.println("Pause pressed");});
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
}
