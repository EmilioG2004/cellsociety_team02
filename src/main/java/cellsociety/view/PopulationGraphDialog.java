package cellsociety.view;

import cellsociety.controller.SimulationPresenter.PopulationSnapshot;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Small dialog that displays population counts over time for a single simulation slot.
 */
public class PopulationGraphDialog {

  private static final int DIALOG_WIDTH = 540;
  private static final int DIALOG_HEIGHT = 360;

  private final Stage myStage;
  private final NumberAxis myGenerationAxis;
  private final NumberAxis myPopulationAxis;
  private final LineChart<Number, Number> myChart;

  public PopulationGraphDialog() {
    myStage = new Stage();
    myStage.initModality(Modality.APPLICATION_MODAL);

    myGenerationAxis = new NumberAxis();
    myGenerationAxis.setLabel("Generation");
    myPopulationAxis = new NumberAxis();
    myPopulationAxis.setLabel("Population");

    myChart = new LineChart<>(myGenerationAxis, myPopulationAxis);
    myChart.setAnimated(false);
    myChart.setCreateSymbols(false);

    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> myStage.close());
    HBox footer = new HBox(closeButton);
    footer.setAlignment(Pos.CENTER_RIGHT);
    footer.setPadding(new Insets(8, 12, 8, 12));

    BorderPane root = new BorderPane();
    root.setCenter(myChart);
    root.setBottom(footer);
    root.setPadding(new Insets(8));

    myStage.setScene(new Scene(root, DIALOG_WIDTH, DIALOG_HEIGHT));
  }

  public void showForSlot(int slotIndex, List<PopulationSnapshot> history) {
    if (history == null || history.isEmpty()) {
      throw new IllegalStateException("No population history available for slot " + slotIndex);
    }

    myChart.getData().clear();
    myStage.setTitle("Population Graph - Slot " + (slotIndex + 1));

    Set<Integer> allStates = new TreeSet<>();
    for (PopulationSnapshot snapshot : history) {
      allStates.addAll(snapshot.getCounts().keySet());
    }

    Map<Integer, XYChart.Series<Number, Number>> seriesByState = new LinkedHashMap<>();
    for (Integer state : allStates) {
      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      series.setName("State " + state);
      seriesByState.put(state, series);
    }

    for (PopulationSnapshot snapshot : history) {
      int generation = snapshot.getGeneration();
      Map<Integer, Integer> counts = snapshot.getCounts();
      for (Integer state : allStates) {
        int population = counts.getOrDefault(state, 0);
        seriesByState.get(state).getData().add(new XYChart.Data<>(generation, population));
      }
    }

    myChart.getData().addAll(seriesByState.values());
    myStage.showAndWait();
  }
}
