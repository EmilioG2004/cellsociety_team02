package cellsociety.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

public class ConfigLoaderTest {

  private static final String GOL_GLIDER =
      "src/main/resources/cellsociety/configs/gol_glider.xml";

  @org.testng.annotations.Test
  public void loadConfig_validGameOfLifeConfig_createsCorrectGrid() throws Exception {
    File file = new File(GOL_GLIDER);

    ConfigLoader loader = new ConfigLoader();
    SimulationConfig config = loader.loadConfig(file);

    assertEquals("GameOfLife", config.getSimulationType());
    assertEquals(5, config.getRows());
    assertEquals(20, config.getCols());
    assertEquals(1, config.getInitialStates()[1][1]);
  }

}
