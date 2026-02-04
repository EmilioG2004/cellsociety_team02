package cellsociety.Model;

import cellsociety.Model.GameRules.ConwayGameOfLifeRule;
import cellsociety.config.SimulationConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimulationFactoryTest {
    private SimulationFactory simulationFactory;
    private SimulationConfig simulationConfig;
    private Simulation simulation;
    private Grid grid;
    private GameRule rules;

    @BeforeEach
    void setUp() {
        simulationFactory = new SimulationFactory();
        simulationConfig = new SimulationConfig("GameOfLife", "Test Simulation", "Test Author", "Test Description", 3, 3, new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, new HashMap<>());
    }
    //z - zero: empty/minimal state
    @Test
    void testCreateSimulation_emptyGrid_returnsEmptyGrid() {
        SimulationConfig simulationConfig = new SimulationConfig("GameOfLife", "Test Simulation", "Test Author", "Test Description", 0, 0, new int[][] {}, new HashMap<>());
        simulation = SimulationFactory.createSimulation(simulationConfig);
        assertNotNull(simulation);
        assertEquals(0, simulation.getGrid().getRows());
        assertEquals(0, simulation.getGrid().getCols());
    }
    //o - one: single element
    @Test
    void testCreateSimulation_singleElement_returnsGridWithSingleElement() {
        SimulationConfig simulationConfig = new SimulationConfig("GameOfLife", "Test Simulation", "Test Author", "Test Description", 1, 1, new int[][] {{1}}, new HashMap<>());
        simulation = SimulationFactory.createSimulation(simulationConfig);
        assertNotNull(simulation);
        assertEquals(1, simulation.getGrid().getRows());
        assertEquals(1, simulation.getGrid().getCols());
        assertEquals(1, simulation.getGrid().getCellState(0, 0));
    }
    //m - many: multiple elements
    @Test
    void testCreateSimulation_multipleElements_returnsGridWithMultipleElements() {
        SimulationConfig simulationConfig = new SimulationConfig("GameOfLife", "Test Simulation", "Test Author", "Test Description", 3, 3, new int[][] {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}}, new HashMap<>());
        simulation = SimulationFactory.createSimulation(simulationConfig);
        assertNotNull(simulation);
        assertEquals(3, simulation.getGrid().getRows());
        assertEquals(3, simulation.getGrid().getCols());
        assertEquals(0, simulation.getGrid().getCellState(0, 0));
        assertEquals(1, simulation.getGrid().getCellState(0, 1));
        assertEquals(0, simulation.getGrid().getCellState(0, 2));
        assertEquals(0, simulation.getGrid().getCellState(1, 0));
        assertEquals(1, simulation.getGrid().getCellState(1, 1));
        assertEquals(0, simulation.getGrid().getCellState(1, 2));
        assertEquals(0, simulation.getGrid().getCellState(2, 0));
        assertEquals(1, simulation.getGrid().getCellState(2, 1));
        assertEquals(0, simulation.getGrid().getCellState(2, 2));
    }
    //i - interface: verify proper interface usage
    @Test
    void testCreateSimulation_returnsSimulationWithGridAndRule() {
        simulation = SimulationFactory.createSimulation(simulationConfig);
        assertNotNull(simulation);
        assertNotNull(simulation.getGrid());
        assertNotNull(simulation.getRule());
        assertEquals(simulationConfig.getRows(), simulation.getGrid().getRows());
        assertEquals(simulationConfig.getCols(), simulation.getGrid().getCols());

    }
    //e - error: invalid simulation type
    @Test
    void testCreateSimulation_invalidSimulationType_throwsIllegalArgumentException() {
        SimulationConfig simulationConfig = new SimulationConfig("InvalidSimulationType", "Test Simulation", "Test Author", "Test Description", 10, 10, new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, new HashMap<>());
        assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(simulationConfig));
    }
    //e - error; initial grid and rows/cols mismatch
    @Test
    void testCreateSimulation_initialGridAndRowsColsMismatch_throwsIllegalArgumentException() {
        SimulationConfig simulationConfig = new SimulationConfig("GameOfLife", "Test Simulation", "Test Author", "Test Description", 10, 10, new int[][] {{0, 0, 0}, {0, 0, 0}}, new HashMap<>());
        assertThrows(IllegalArgumentException.class, () -> SimulationFactory.createSimulation(simulationConfig));
    }
    //s - simple: typical use cases
    @Test
    void testCreateSimulation_typicalUseCase_returnsSimulationWithGridAndConwayGameOfLifeRule() {
        SimulationConfig simulationConfig = new SimulationConfig("GameOfLife", "Test Simulation", "Test Author", "Test Description", 3, 3, new int[][] {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}}, new HashMap<>());
        simulation = SimulationFactory.createSimulation(simulationConfig);
        assertNotNull(simulation);
        assertEquals(3, simulation.getGrid().getRows());
        assertEquals(3, simulation.getGrid().getCols());
        assertEquals(0, simulation.getGrid().getCellState(0, 0));
        assertEquals(1, simulation.getGrid().getCellState(0, 1));
        assertEquals(0, simulation.getGrid().getCellState(0, 2));
        assertEquals(0, simulation.getGrid().getCellState(1, 0));
        assertEquals(1, simulation.getGrid().getCellState(1, 1));
        assertEquals(0, simulation.getGrid().getCellState(1, 2));
        assertEquals(0, simulation.getGrid().getCellState(2, 0));
        assertEquals(1, simulation.getGrid().getCellState(2, 1));
        assertEquals(0, simulation.getGrid().getCellState(2, 2));
        assertEquals(simulation.getRule().getClass(), ConwayGameOfLifeRule.class);
    }

}
