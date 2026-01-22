# Breakout Abstractions Lab Discussion
#### Names
Michael Lei
Ixa Sigler


### Block

This superclass's purpose as an abstraction:
```java
public class Block {
  // called by Game/Level when something collides with this block
  public int tryHit(HitInfo hit, GameManager gm)
  // returns 0 if hit is accepted and applied, -1 if hit has no effect (e.g., shielded)

  public boolean isDestroyed()
  // whether this block should be removed from the level
  
  // used for multi hit bricks to adjust size
  public double getSize()
  public void setSize()
}
```

#### Subclasses

Each subclass's high-level behavorial differences from the superclass
* BiggerBallBrick - add additional behavior on hit, ask GameManager to increase ball sizes
* NewBallBrick - similar to BiggerBallBrick, ask GameManager to spawn new ball
* ShieldedBrick - can only be hit by certain angle(s)
* HardyBrick - take multiple hits and shrink on hit


#### Affect on Game/Level class

Which methods are simplified by using this abstraction and why
* killBrick - by using a more adept HitInfo, allows for more precise functionality e.g. differentiating between ball hit and managerial deletion (on level reset)


### Power-up

This superclass's purpose as an abstraction:
```java
public class PowerUp {
  public void apply(GameManager gm, HitInfo hit)
  // applies this power-up’s effect to the game (e.g., spawn ball, grow balls, add shield)
  // HitInfo can be used for location-based effects (spawn at collision point)

  public boolean isInstant()
  // returns true if effect happens immediately and ends, false if it persists and must be tracked as “active” (timed/temporary power-up)
}
```

#### Subclasses

Each subclass's high-level behavorial differences from the superclass
BiggerBallPowerUp - tells GameManager to increase size of current ball(s)
NewBallPowerUp - tells GameManager to add new ball (probably at collision point)
BiggerShieldPowerUp - tells GameManager to increase shield size


#### Affect on Game/Level class

Which methods are simplified by using this abstraction and why
All brick related methods (esp. killBrick and Brick.tryHit) are simplified because currently powerups are baked into Brick collision. With new powerup classes the BiggerBallBrick and NewBallBrick classes can actually be removed