# Refactoring Lab Discussion
#### NAMES
Ixa Sigler, Emilio Garcia, Michael Lei
#### TEAM 2


## Design Principles

* Open/Close
Ixa: My interpretation is that classes, objects etc should be *open* to extend but *closed* to modify. In practice this means using lots of abstractions, subclasses, and composition, so new functionality is added by plugging in new components instead of editing stable code.
Emilio: My interpretation is that classes and code should be written in such a way that they do not need to be modified, and the ability to extend functionality should be baked in. This means being intentional about design choices and making extensibility a core principal fo the design. 
Michael: My interpretation is that classes should be written so that classes that depend on them shouldn't have to be modified if you add new behavior to class. For us this is ideal so we can make different simulation types like GoL, water and percolation without touching code for grids, cells, etc.
* Liskov Substution
Ixa: My interpretation is that subclasses should only be extending or adding behavior and not changing/breaking behavior of its superclass. E.g. if I make a superclass Vehicle and a subclass Car, the Car should have all the basic vehicle functionality intact AND new behavior like honking the horn.
Emilio: My interpretation of the Liskov Substitution Principle is that subclasses need to be written in such a way that they take special care to not change or break any implementations used by the superclass. It is important to make sure you are not introducing unintended side effects with specific subclass implementations.
Michael: My interpretation of the LSP is that classes should be replacable by the classes that inherit from those classes without breaking functionality. Basically all the functionality that the parent class has, the child class should also have along with any additional child specific behavior.
### Current Abstractions

#### Abstraction #1 Game Rule
* Current embodiment of principles
    * Open for extension:
      New simulation types just extend GameRule and implement getNextState() + getStateColors()
      Each rule can have unique parameters via the parameters map
      Custom neighbor logic via NeighborStrategy injection
    * Closed for modification:
      Adding a new simulation requires zero changes to existing code
      The grid doesn't need updates when you add new rules
      Core contract (getNextState) remains stable
    * Liskov Substitution Principle:
      Any subclass can replace GameRule without breaking client code because all subclasses must implement getNextState() with the same signature, accepting current state + neighbors and returning an int state
      The abstraction doesn't make assumptions about how rules work, only what they produce

* Improved embodiment of principles
    * Open/Closed Principle:
      making parameters immutable to prevent subclasses from violating the closed principle by modifying shared state
      Add validation in constructor to ensure neighborStrategy is not null,
      prevent subclasses from creating invalid states
    * Liskov Substitution Principle:
      validate return value constraints to prevent subclasses from breaking client expectations
      Make countNeighborsWithState() final to prevent subclasses from changing
      its semantics, ensuring consistent behavior across all rules
#### Abstraction #2 Grid View
* Current embodiment of principles
    * Open for extension: Other Grid Views can be easily implemented following the interface utilizing different internals as need be. Different views for different simulations can be implemented with unique rendering logic as need be.
    * Closed for Modification: Adding new gridviews for different simulations does not require any change to existing code, just adding new code. The core contract remains stable.
    

* Improved embodiment of principles



### New Abstractions

#### Abstraction #1 CellStates
* Description
Abstraction representing the valid states for a cell, ie there would be a different CellStates for Game of life and Percolation. Game of Life cell states should be an enum including DEAD and ALIVE while percolation cell states should include OPEN, CLOSED, FULL.

* How it supports making it easier to implement new features
Right no matter for what simulation we have, cells always store their current and next state as an integer, and we just parse those integers differently for different simulation types.
This is bad design because a state of 0 would mean different things for different simulations and would require a lot of extra logic from higher level classes. If we instead have cell store its state with a simulation specific enum, it'll be easier to decipher what a state means later on and also make it easy to interchange state types in between simulations.

#### Abstraction #2 CellView
* Description
Another abstraction that would help is an abstraction to visually represent a cell, so like a CellView abstraction. 
* How it supports making it easier to implement new features
When implementing new features specifically regarding the different ways to interact with cells individually. This would also make certain features, like changing cell shape and interacting with individual cells to set them on/off far easier. 
