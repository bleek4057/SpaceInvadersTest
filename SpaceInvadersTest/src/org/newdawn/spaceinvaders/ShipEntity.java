package org.newdawn.spaceinvaders;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class ShipEntity extends Entity implements Damageable{
	/** The game in which the ship exists */
	private Game game;
        private int health;
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param game The game in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public ShipEntity(Game game,String ref,int x,int y, int _health) {
		super(ref,x,y);
		health = _health;
		this.game = game;
	}
	
	/**
	 * Request that the ship move itself based on an elapsed ammount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > 750)) {
			return;
		}
		
		super.move(delta);
	}
	
	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		// if its an alien, notify the game that the player
		// is dead
		if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
	}

        @Override
        public void takeDamage(int _damage) {
            health -= _damage;
            System.out.println("Ship damage");
            if(health <= 0){
                die();
            }
        }
        
        @Override 
        public void die(){
            game.notifyDeath();
        }
        
        //Must be overriden but the ship is never destroyed
        @Override
        public void onDestroy(){
            
        }
}