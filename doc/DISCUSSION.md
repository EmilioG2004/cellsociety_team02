# Testing Discussion
### NAMES
Ixa Sigler, Emilio Garcia, Michael Lei
### TEAM 2

#Planned Test Scenarios for View
*Given: No simulation is currently loaded
*When: The user steps one generation forward/backward
*Then: The generation count should stay unchanged

*Given: No simulation is currently loaded
*When: The user presses the start button
*Then: There should be no change in state, generation count should stay unchanged, grid view should still be unpopulated

*Given: The simulation is currently running and going backwards
*When: The simulation reaches the initial state which is Generation 0
*Then:  There are no more generations to go back to so the simulation should remain running but cell states should no longer change.

*Given: The simulation is currently running backwards and has reached and is stuck on generation 0
*When: The user swaps direction to forward
*Then: The simulation should advance forward and the simulation should start just like how if the user starts the simulation immediately after loading the config in.

*Given: The simulation is currently running backwards and has reached and is stuck on generation 0
*When: The user swaps direction to forward
*Then: The simulation should advance forward and the simulation should start just like how if the user starts the simulation immediately after loading the config in.

*Given: The simulation is currently paused
*When: The user steps one generation forward
*Then: The generation count should move up 1 and the grid cell states should update

*Given: The simulation is currently paused and it's not currently on generation 0
*When: The user steps one generation backward
*Then: The generation count should move down 1 and the grid cell states should update

*Given: The simulation is currently paused and it's on generation 0
*When: The user steps one generation backward
*Then: The generation count and cell states should remain unchanged

*Given: The simulation does not have an uploaded configuration file, that is the application just opened and there is no file yet
*When: A bad file is uploaded, ie a non xml file or a malformed xml file
*Then:  An error should be caught and displayed to the user in the UI window

*Given: The simulation is currently running in either direction 
*When: The save button is clicked
*Then: The simulation pauses 

*Given: The simulation has no initial XML configuration uploaded
*When: Multiple data files are selected and uploaded 
*Then: The UI will display an error showing that only one file can be uploaded into configuration at a time

*Given: The simulation is currently running
*When: The reset button is clicked
*Then: All settings and labels should return to initial state, except for current options like speed
