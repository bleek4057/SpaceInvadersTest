package org.newdawn.spaceinvaders;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic. 
 * 
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * 
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 * 
 * @author Kevin Glass
 */
public class Game extends Canvas {
        public static Game instance;
        
	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;
	/** The list of all the entities that exist in our game */
	private ArrayList entities = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	private ArrayList removeList = new ArrayList();
	/** The entity representing the player */ 
	private Entity ship;
	/** The speed at which the player's ship should move (pixels/sec) */
	private double moveSpeed = 300;
	/** The time at which last fired a shot */
	private long lastFire = 0;
	/** The interval between our players shot (ms) */
	private long firingInterval = 500;
	/** The number of aliens left on the screen */
	private int alienCount;
	
	/** The message to display which waiting for a key press */
	private String message = "";
	/** True if we're holding up game play until a key has been pressed */
	private boolean waitingForKeyPress = true;
	/** True if the left cursor key is currently pressed */
	private boolean leftPressed = false;
	/** True if the right cursor key is currently pressed */
	private boolean rightPressed = false;
	/** True if we are firing */
	private boolean firePressed = false;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	private boolean logicRequiredThisLoop = false;
	
        //Enumerates the different ammo types the player can fire
        public static enum ShotType { SINGLE, DOUBLE, TRIPLE, BOMB }
        private ShotType selectedShotType = ShotType.SINGLE;
        
        //List of shots fired by the player this frame. Needed for multiammo types
        private ArrayList shots = new ArrayList();

        
	/**
	 * Construct our game and set it running.
	 */
	public Game() {   
                initSingleton();
                
		// create a frame to contain our game
		JFrame container = new JFrame("Space Invaders 101");
		
		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,800));
		panel.setLayout(null);
		
		// setup our canvas size and put it into the content of the frame
		setBounds(0,0,800,800);
		panel.add(this);
		
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(new KeyInputHandler());
		
		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		// initialise the entities in our game so there's something
		// to see at startup
		initEntities();
	}
	
        private void initSingleton(){
            if(instance == null) instance = this;
        }
        
        public ArrayList getShots(){
            return shots;
        }
        
	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	private void startGame() {
		// clear out any existing entities and intialise a new set
		entities.clear();
		initEntities();
		
		// blank out any keyboard settings we might currently have
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
	}
	
	/**
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 */
	private void initEntities() {
		// create the player ship and place it roughly in the center of the screen
		ship = new ShipEntity(this,"sprites/ship.gif",370,550, 3);
		entities.add(ship);
		
		// create a block of aliens (5 rows, by 12 aliens, spaced evenly)
		alienCount = 0;
		for (int row=0;row<5;row++) {
			for (int x=0;x<12;x++) {
                            Entity alien = null;
                            if(row == 0){
                                //Adds heavy enemies to the back row
                                alien = new HighHealthAlien(this,"sprites/largeAlien.gif",100+(x*50),(50)+row*30, 2);
                            }else if (row == 1){
                                alien = new ProjectileAlien(this, "sprites/projectileAlien.gif", 100+(x*50),(50)+row*30, 1, 6000);
                            }else{
                                //The rest are regular enemies
                                alien = new AlienEntity(this,"sprites/alien.gif",100+(x*50),(50)+row*30, 1);
                            }
                            entities.add(alien);
                            alienCount++;
			}
		}
                System.out.println(alienCount);
	}
	
	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}
	
	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 * 
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}
	
	/**
	 * Notification that the player has died. 
	 */
	public void notifyDeath() {
		message = "Oh no! They got you, try again?";
		waitingForKeyPress = true;
	}
	
	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 */
	public void notifyWin() {
		message = "Well done! You Win!";
		waitingForKeyPress = true;
	}
	
	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled() {
		// reduce the alient count, if there are none left, the player has won!
		alienCount--;
		
		if (alienCount == 0) {
			notifyWin();
		}
		
		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}
	
        //Returns a list of enemies within a specified radius of the given point
        public ArrayList findEnemyInRange(int x, int y, int r){
            ArrayList enemiesInRange = new ArrayList();
            double dist;
            int enemyX, enemyY;
            for(int i = 0; i < entities.size(); i++){
                Entity entity = (Entity)entities.get(i);
                if(entity instanceof AlienEntity){
                    AlienEntity alien = (AlienEntity)entity;
                    enemyX = alien.getX();
                    enemyY = alien.getY();
                    
                    dist = ( (enemyX - x) * (enemyX - x)) + ((enemyY - y) * (enemyY - y));
                    
                    if(dist <= (r * r)){
                        enemiesInRange.add(alien);
                    }
                }
            }
            
            return enemiesInRange;
        }
	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this 
	 * point, i.e. has he/she waited long enough between shots
	 */
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}
		                
                //Leave this outside the swith statement for cleanliness
                lastFire = System.currentTimeMillis();
                int numShots = 1;
                
                switch(selectedShotType){
                    case SINGLE:
                        numShots = 1;
                        break;
                    case DOUBLE:
                        numShots = 2;
                        break;
                    case TRIPLE:
                        numShots = 3;
                        break;
                    case BOMB:
                        numShots = 1;
                        break;
                }
                
                //Fire using defined properties
                if(numShots % 2 == 0){
                    fireStraight(ship.getX(), ship.getY(), numShots, selectedShotType, 1, ship);
                } else {
                    fireSpread(ship.getX(), ship.getY(), numShots, selectedShotType, 1, ship);
                }
	}
	
        public void fireStraight(int x, int y, int _numShots, ShotType _shotType, int _direction, Entity _owner){
            String shotTexture = "shot";
            boolean playerProj = _owner instanceof ShipEntity;

            for(int i = 0; i < _numShots; i++){
                //Adds a positive angle for even numbers and a negative angle for odd numbers
                switch(_shotType){
                    case DOUBLE:
                    case SINGLE:
                        shotTexture = playerProj ? "shot" : "alienShot";
                        shots.add(new StraightShot(this,"sprites/" + shotTexture + ".gif", x + (i * 20), y, 6, 
                                90 * _direction, 1, playerProj));
                        break;
                    case TRIPLE:
                        shotTexture = "roundShot";
                        shots.add(new StraightShot(this,"sprites/" + shotTexture + ".gif", x + 10, y, 3,
                                90 * _direction, 1, playerProj));
                        break;
                    case BOMB:
                        shotTexture = "roundShot";
                        shots.add(new BombShot(this,"sprites/" + shotTexture + ".gif", x + 10, y, 2, 
                                90* _direction, 10f, 1, playerProj));
                        break;
                }
            }
                
            //Sends all shots fired this frame to the entities list for updates
            for(int i = 0; i < shots.size(); i++){  
                entities.add(shots.get(i));
            }
        }
        
        /*Fires one or more shots at angles based on the number of shots requested
          Direction signifies whether the bullet is being fired up by the player or down 
            by an alien [-1, 1]
        */
        public void fireSpread(int x, int y, int _numShots, ShotType _shotType, int _direction, Entity _owner){
            float fireAngle = 2 * _numShots;
            String shotTexture = "shot";
            boolean playerProj = _owner instanceof ShipEntity;

            for(int i = 0; i < _numShots; i++){
                //Adds a positive angle for even numbers and a negative angle for odd numbers
                switch(_shotType){
                    case SINGLE:
                        shotTexture = playerProj ? "shot" : "alienShot";
                        shots.add(new StraightShot(this,"sprites/" + shotTexture + ".gif", x + 10, y, 6, 
                                (90 * _direction) + ((( i % 2 == 0) ? i : -i-1 ) * fireAngle ), 1, playerProj));
                        break;
                    case TRIPLE:
                        shotTexture = "roundShot";
                        shots.add(new StraightShot(this,"sprites/" + shotTexture + ".gif", x + 10, y, 3,
                                (90 * _direction) + ((( i % 2 == 0) ? i : -i-1 ) * fireAngle ), 1, playerProj));
                        break;
                    case BOMB:
                        shotTexture = "roundShot";
                        shots.add(new BombShot(this,"sprites/" + shotTexture + ".gif", x + 10, y, 2, 
                                (90* _direction) + ((( i % 2 == 0) ? i : -i-1 ) * fireAngle * _direction), 10f, 1, playerProj));
                        break;
                }
            }
                
            //Sends all shots fired this frame to the entities list for updates
            for(int i = 0; i < shots.size(); i++){  
                entities.add(shots.get(i));
            }
            
        }
        /**
         * Changes the type of shot the Ship fires based on
         * key presses */
        private void updateShotType(ShotType _shotType){
            selectedShotType = _shotType;
        }
        
	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:	
 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		
		// keep looping round til the game ends
		while (gameRunning) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			// Get hold of a graphics context for the accelerated 
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(new Color(209, 238, 84));
			g.fillRect(0,0,800,800);
			
			// cycle round asking each entity to move itself
			if (!waitingForKeyPress) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					
					entity.move(delta);
				}
			}
			
			// cycle round drawing all the entities we have in the game
                        // Folding update loop for projectile enemies into this loop for effeciency
			for (int i=0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);
				
				entity.draw(g);
                                
                                if(entity instanceof ProjectileAlien){
                                    ProjectileAlien projAlien = (ProjectileAlien)entity;
                                    projAlien.tryFire();
                                }
			}
			
			// brute force collisions, compare every entity against
			// every other entity. If any of them collide notify 
			// both entities that the collision has occured
			for (int p=0;p<entities.size();p++) {
				for (int s=p+1;s<entities.size();s++) {
					Entity me = (Entity) entities.get(p);
					Entity him = (Entity) entities.get(s);
					
					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					}
				}
			}
			
			// remove any entity that has been marked for clear up
			entities.removeAll(removeList);
			removeList.clear();

			// if a game event has indicated that game logic should
			// be resolved, cycle round every entity requesting that
			// their personal logic should be considered.
			if (logicRequiredThisLoop) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				}
				
				logicRequiredThisLoop = false;
			}
			
			// if we're waiting for an "any key" press then draw the 
			// current message 
			if (waitingForKeyPress) {
				g.setColor(Color.black);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("Press any key",(800-g.getFontMetrics().stringWidth("Press any key"))/2,300);
			}
			
			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over
			g.dispose();
			strategy.show();
			
			// resolve the movement of the ship. First assume the ship 
			// isn't moving. If either cursor key is pressed then
			// update the movement appropraitely
			ship.setHorizontalMovement(0);
			
			if ((leftPressed) && (!rightPressed)) {
				ship.setHorizontalMovement(-moveSpeed);
			} else if ((rightPressed) && (!leftPressed)) {
				ship.setHorizontalMovement(moveSpeed);
			}
			
			// if we're pressing fire, attempt to fire
			if (firePressed) {
				tryToFire();
			}
			
                        
                        shots.clear();
                        
			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try { Thread.sleep(10); } catch (Exception e) {}
		}
	}
	
	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right 
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through 
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 * 
	 * @author Kevin Glass
	 */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		private int pressCount = 1;
		
		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed 
		 */
		public void keyPressed(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "press"
			if (waitingForKeyPress) {
				return;
			}
                        
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
                        
			//Handle switching between fire modes
                        if(e.getKeyCode() == KeyEvent.VK_SPACE){
                            updateShotType(ShotType.SINGLE);
                            firePressed = true;
                        }
                        
                        if(e.getKeyCode() == KeyEvent.VK_CONTROL){
                            updateShotType(ShotType.TRIPLE);
                            firePressed = true;
                        }
                        
                        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                            updateShotType(ShotType.DOUBLE);
                            firePressed = true;
                        }
                        
                        if(e.getKeyCode() == KeyEvent.VK_Z){
                            updateShotType(ShotType.BOMB);
                            firePressed = true;
                        }
                        

		} 
		
		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released 
		 */
		public void keyReleased(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "released"
			if (waitingForKeyPress) {
				return;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_Z) {
				firePressed = false;
			}
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed. 
		 */
		public void keyTyped(KeyEvent e) {
			// if we're waiting for a "any key" type then
			// check if we've recieved any recently. We may
			// have had a keyType() event from the user releasing
			// the shoot or move keys, hence the use of the "pressCount"
			// counter.
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					// since we've now recieved our key typed
					// event we can mark it as such and start 
					// our new game
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
			
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}
	
	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {
		Game g = new Game();

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		g.gameLoop();
	}
}
