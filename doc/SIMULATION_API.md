# Cell Society API Lab Discussion
### NAMES
Ixa Sigler, Emilio Garcia, Michael Lei
### TEAM 2


## Current Simulation Model API

* Identified Public Classes/Methods

### Method Categorization (Current API)
The following should not be part of the Simulation API:
* Pretty much everything in the view package: getMyUploadButton(), getMyStartButton(), getMyPauseButton(), getMySpeedUpButton(), getMySlowDownButton(), getMyStepButton(), getMySaveButton(), getMyGridView()
These expose UI internals and should be made private.
* Cell.updateNextState(GameRule rules, List<ReadOnlyCell> neighbors)
This involves too much passing around and complex codependency.
* Grid.setCellsState(int[][] state) is too powerful and should be made into a constructor or factory
* Grid.updateAllCells(GameRule rules) - rules should probably be handled by simulation itself and not Grid
Should be made internal:


## Wish Simulation Model API

* Goals

* Contract
    * Services

    * Abstractions and their Methods

    * Exceptions





## API Task Description

* English

* Java
 
 