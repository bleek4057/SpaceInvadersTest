/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.newdawn.spaceinvaders;

/**
 *
 * @author John
 */
public class ProjectileAlien extends AlienEntity{
    //How long the alien waits between each shot
    private float firingInterval;
    private long lastFire;
    private String shotTexture = "roundShot";
    
    public ProjectileAlien(Game game, String ref, int x, int y, int _health, float _firingInterval) {
        super(game, ref, x, y, _health);
        firingInterval = _firingInterval;
        System.out.println("Alien start time: " + System.currentTimeMillis());
        lastFire = System.currentTimeMillis();
    }
    
    private void fire(){
        lastFire = System.currentTimeMillis();
        Game.instance.fire(getX(), getY(), 1, Game.ShotType.SINGLE, 1);
        //Game.instance.getShots().add(new StraightShot(super.game, "sprites/" + shotTexture + ".gif", getX() + 10,getY() + 30, 1, 90, -1));
    }
    
    public void tryFire(){
       //System.out.println("Time: " + System.currentTimeMillis() + ", lastfire + fireinterval: "  + (long)(lastFire + firingInterval) + ", fire interval: " + firingInterval + ", last fire: " + lastFire);
        if (System.currentTimeMillis() > lastFire + firingInterval) {
            fire();
        }        
    }
}
