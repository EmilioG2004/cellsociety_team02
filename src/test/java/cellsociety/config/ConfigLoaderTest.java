package cellsociety.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

public class ConfigLoaderTest {

  private static final String GOL_GLIDER =
      "data/gol_glider.xml";

  private static final String GOL_MALFORMED =
      "data/gol_malformed.xml";

  private static final String TEST_PARAMETERS =
      "data/test_parameters.xml";

  @Test
  public void loadConfig_validGameOfLifeConfig_createsCorrectGrid() throws Exception {
    File file = new File(GOL_GLIDER);

    ConfigLoader loader = new ConfigLoader();
    SimulationConfig config = loader.loadConfig(file);

    assertEquals("GameOfLife", config.getSimulationType());
    assertEquals(5, config.getRows());
    assertEquals(20, config.getCols());
    assertEquals(1, config.getInitialStates()[1][1]);
  }

  @Test
  public void loadConfig_validConfig_readsMetadataCorrectly() throws Exception {
    File file = new File(GOL_GLIDER);

    ConfigLoader loader = new ConfigLoader();
    SimulationConfig config = loader.loadConfig(file);

    assertEquals("Test Glider", config.getTitle());
    assertEquals("Ixa Sigler", config.getAuthor());
    assertFalse(config.getDescription().isBlank());
  }

  @Test
  public void loadConfig_validConfig_createsCorrectStateDimensions() throws Exception {
    File file = new File(GOL_GLIDER);

    ConfigLoader loader = new ConfigLoader();
    SimulationConfig config = loader.loadConfig(file);

    int[][] states = config.getInitialStates();

    assertEquals(5, states.length);
    assertEquals(20, states[0].length);

    assertEquals(0, states[0][0]);
  }

  @Test
  public void loadConfig_malformedGridSize_throwsException() {
    File file = new File(GOL_MALFORMED);

    ConfigLoader loader = new ConfigLoader();

    Exception e = assertThrows(Exception.class, () -> loader.loadConfig(file));

    // Optional: sanity check the error message is about dimensions/size
    assertTrue(
        e.getMessage() == null
            || e.getMessage().toLowerCase().contains("row")
            || e.getMessage().toLowerCase().contains("col")
            || e.getMessage().toLowerCase().contains("dimension")
            || e.getMessage().toLowerCase().contains("size"),
        "Expected error message to mention grid size/dimensions, but was: " + e.getMessage()
    );
  }

  @Test
  public void loadConfig_validConfig_readsParametersIntoMap() throws Exception {
    File file = new File(TEST_PARAMETERS);

    ConfigLoader loader = new ConfigLoader();
    SimulationConfig config = loader.loadConfig(file);

    var params = config.getParameters();

    assertNotNull(params);
    assertEquals(2, params.size());

    assertTrue(params.containsKey("Apple"));
    assertEquals("1", params.get("Apple"));

    assertTrue(params.containsKey("Banana"));
    assertEquals("fruit", params.get("Banana"));
  }

}
