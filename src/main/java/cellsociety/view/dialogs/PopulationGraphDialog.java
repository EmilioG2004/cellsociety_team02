package cellsociety.view.dialogs;

import static cellsociety.view.constants.PopulationGraphUiConstants.AXIS_LABEL_GENERATION;
import static cellsociety.view.constants.PopulationGraphUiConstants.AXIS_LABEL_POPULATION;
import static cellsociety.view.constants.PopulationGraphUiConstants.CLOSE_BUTTON_TEXT;
import static cellsociety.view.constants.PopulationGraphUiConstants.DIALOG_HEIGHT;
import static cellsociety.view.constants.PopulationGraphUiConstants.DIALOG_WIDTH;
import static cellsociety.view.constants.PopulationGraphUiConstants.FOOTER_PADDING_BOTTOM;
import static cellsociety.view.constants.PopulationGraphUiConstants.FOOTER_PADDING_LEFT;
import static cellsociety.view.constants.PopulationGraphUiConstants.FOOTER_PADDING_RIGHT;
import static cellsociety.view.constants.PopulationGraphUiConstants.FOOTER_PADDING_TOP;
import static cellsociety.view.constants.PopulationGraphUiConstants.NO_HISTORY_PREFIX;
import static cellsociety.view.constants.PopulationGraphUiConstants.ROOT_PADDING;
import static cellsociety.view.constants.PopulationGraphUiConstants.STATE_SERIES_PREFIX;
import static cellsociety.view.constants.PopulationGraphUiConstants.TITLE_PREFIX;

import cellsociety.controller.multiview.SimulationPresenter.PopulationSnapshot;
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
 *
 * Purpose: provide an at-a-glance time series for state populations in one slot.
 * Assumptions: history is non-empty and each snapshot contains non-null counts.
 * Dependencies: JavaFX chart APIs and {@link PopulationSnapshot}.
 *
 * @author Emilio Garcia
 * @author Team 02
 */
public class PopulationGraphDialog {

  private final Stage myStage;
  private final NumberAxis myGenerationAxis;
  private final NumberAxis myPopulationAxis;
  private final LineChart<Number, Number> myChart;

  /**
   * Creates a modal graph dialog with chart and close action.
   */
  public PopulationGraphDialog() {
    myStage = new Stage();
    myStage.initModality(Modality.APPLICATION_MODAL);

    myGenerationAxis = new NumberAxis();
    myGenerationAxis.setLabel(AXIS_LABEL_GENERATION);
    myPopulationAxis = new NumberAxis();
    myPopulationAxis.setLabel(AXIS_LABEL_POPULATION);

    myChart = new LineChart<>(myGenerationAxis, myPopulationAxis);
    myChart.setAnimated(false);
    myChart.setCreateSymbols(false);

    Button closeButton = new Button(CLOSE_BUTTON_TEXT);
    closeButton.setOnAction(e -> myStage.close());
    HBox footer = new HBox(closeButton);
    footer.setAlignment(Pos.CENTER_RIGHT);
    footer.setPadding(new Insets(
        FOOTER_PADDING_TOP,
        FOOTER_PADDING_RIGHT,
        FOOTER_PADDING_BOTTOM,
        FOOTER_PADDING_LEFT
    ));

    BorderPane root = new BorderPane();
    root.setCenter(myChart);
    root.setBottom(footer);
    root.setPadding(new Insets(ROOT_PADDING));

    myStage.setScene(new Scene(root, DIALOG_WIDTH, DIALOG_HEIGHT));
  }

  /**
   * Displays a population chart for one slot.
   *
   * @param slotIndex zero-based slot index
   * @param history ordered population snapshots for that slot
   * @throws IllegalStateException when history is null or empty
   */
  public void showForSlot(int slotIndex, List<PopulationSnapshot> history) {
    if (history == null || history.isEmpty()) {
      throw new IllegalStateException(NO_HISTORY_PREFIX + slotIndex);
    }

    myChart.getData().clear();
    myStage.setTitle(TITLE_PREFIX + (slotIndex + 1));

    Set<Integer> allStates = new TreeSet<>();
    for (PopulationSnapshot snapshot : history) {
      allStates.addAll(snapshot.getCounts().keySet());
    }

    Map<Integer, XYChart.Series<Number, Number>> seriesByState = new LinkedHashMap<>();
    for (Integer state : allStates) {
      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      series.setName(STATE_SERIES_PREFIX + state);
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
