package org.newdawn.spaceinvaders;

/**
 * An entity representing a shot fired by the player's ship
 * 
 * @author Kevin Glass
 */
public class ShotEntity extends Entity {
	/** The vertical speed at which the players shot moves */
	private double moveSpeed = -200;
	/** The game in which this entity exists */
	private Game game;
	/** True if this shot has been "used", i.e. its hit something */
	private boolean used = false;
        
        private float angle;
        private int damage;
	
	/**
	 * Create a new shot from the player
	 * 
	 * @param game The game in which the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot
	 */
	public ShotEntity(Game game,String sprite,int x,int y, double _speedMod, float _angle, int _damage) {
		super(sprite,x,y);
		
		this.game = game;
		
                damage = _damage;
                moveSpeed /= _speedMod;
                angle = _angle;
		//dy = moveSpeed;
	}

	/**
	 * Request that this shot moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta) {
                super.translate((double)Math.cos(Math.toRadians(angle)) * moveSpeed, (double)Math.sin(Math.toRadians(angle)) * moveSpeed);
                super.move(delta);
                
                // if we shot off the screen, remove ourselfs
		if (y < -100) {
			game.removeEntity(this);
		}
	}
	
        public void rotate(float _angle){
            angle += _angle;
        }
        
        public float getAngle(){
            return angle;
        }
        public void setAngle(float _angle){
            angle = _angle;
        }
	/**
	 * Notification that this shot has collided with another
	 * entity
	 * 
	 * @parma other The other entity with which we've collided
	 */
	public void collidedWith(Entity other) {
		// prevents double kills, if we've already hit something,
		// don't collide
		if (used) {
			return;
		}
		
		// if we've hit an alien, kill it!
		if (other instanceof AlienEntity) {
                        //notify the enemy that it has been damaged
                        Damageable damageableAlien = (Damageable)other;
                        damageableAlien.takeDamage(damage);
                        
                        game.removeEntity(this);
			used = true;
		}
	}
}