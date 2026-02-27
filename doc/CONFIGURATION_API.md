# Cell Society API Lab Discussion
### NAMES Michael Lei, Emilio Garcia, Ixa Sigler
### TEAM 2


## Current Configuration API

* Identified Public Classes/Methods
  * SimulationState can be made a record instead of a final class as it only serves to store data. Additionally, to match the change requirements it will hold a new SimulationPreferences reference rather than explicitly storing the theme, language, etc.
    * public final class SimulationState {
    * public List<SimulationConfig> getActiveSimulations()
    * public int getTheme()
    * public double getSimulationSpeed()
    * public String getLanguage()
    }

  public class ConfigSaver {
  * public void saveState(SimulationState state, File outFile)
    * This method should be kept as part of the API but must be refactored to fit the change requirements.
  * public void saveConfig(SimulationConfig config, File outFile)
    * This method should be kept as a part of the API
  }
  
  * We can make SimulationConfig a record instead of a final class as it consists soley of public get methods
    * public final class SimulationConfig {
    * public SimulationConfig(
    * public String getSimulationType()
    * public String getRuleClass()
    * public String getNeighborStrategyClass()
    * public String getTitle()
    * public String getAuthor()
    * public String getDescription()
    * public int getRows()
    * public int getCols()
    * public int [][] getInitialStates()
    * public Map<String, Double> getParameters()
    }

  * public class ConfigLoader {
    * public record SimulationTypeAndClass(String simulationType, String ruleClass, String neighborStrategyClass) {}
    * public SimulationConfig loadConfig(File xmlFile)
      * This method should be kept as part of the API
    * public record SimulationTypeAndClass(String simulationType, String ruleClass, String neighborStrategyClass)
      * 
    }




## Wish Configuration API

* Goals

Make it easy for the model and view to utilize configuration services for functionality. This includes
presenting User configuration file input in an easily handled way. Ideally, this would also provide
a method to write dynamically to config in order to capture current states and preferences of the simulation.
This should also be extensible to handle other types of configuration files.

View: 
- Provide a service to create a configuration from the current state of a simulation grid
- Provide a service to create an overall representation of all simulations and all settings
- Provide a service to allow writing to files in order for the View to support dynamic user preference updates
- Validate inputs and throw errors in a way that is easily caught by the view

Model:
- Provide a service to parse config into data that the model can read and use
- Provide a service to deliver this data to the model in a way that is easy

* Contract
    * Services
      * Create a config file from current simulation
      * Represent all simulations and preferences
      * Dynamically write to config file to support user dynamic pref updates
      * Parse config file into data
      * Create an easy to pass data bundle for the model

    * Abstractions and their Methods
      * ConfigLoader
        * loadConfig(String fileLocation)
      * ConfigSaver
        * saveConfig(SimulationConfig config, File outFile)
    * Exceptions
        * fileNotFoundException when configloader gets passed an invalid file path
        * invalidFileTypeException when configloader gets passed a valid file path but its not an xml file
        * missingFileElements when configloader gets passed a file missing vile elements e.g. <simulation>
        * invalidConfigParameters when configloader gets passed a valid xml file without missing elements, but some elements have invalid values.




## API Task Description
As a client, the Simulation gets its starting values from a data file:
* English
  * Simulation factory will get a SimulationConfig object. A simulation is defined by its type, the neighbor strategy it uses, the initial cell states of the grid, it's parameters as well as it's grid size. All of which are readily available in the simulation config record it gets passed. And simulation factory would then use reflection to instantiate the correct game rule class with the correct neighbor strategy class. Simulation factory would then create a grid object with the right starting values and then finally instantiate a simulation object with the game rule and grid object. 
* Java
  * SimulationController calls ConfigLoader.loadConfig() with the user-selected XML file, and is returned a SimulationConfig record which is passed to SimulationFactory in the static method SimulationFactory.createSimulation(SimulationConfig config). 

As a client, the View selects a new style to apply to the running simulation, changing its state colors, edge policy, and turns on the grid outline.
* English
  * The View needs to read a SimulationPreferences record containing all of the relevant information and parse it to actualize the settings.
* Java
  * In the program, an uploadPreferences button will exist that when clicked calls Preferences.loadPreferences which provide the view with the data needed to populate it's instance fields regarding the preferences.
