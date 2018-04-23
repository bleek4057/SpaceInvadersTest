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
public class BoomarangShot extends ShotEntity{
    //How many degrees each update cycle the boomarang turns
    private float rotation;
    
    public BoomarangShot(Game game, String sprite, int x, int y, double _speedMod, float _angle, float _rotation, int _damage) {
        super(game, sprite, x, y, _speedMod, _angle, _damage);
        
        rotation = _rotation;
    }
    
    @Override
    public void move(long _delta){
        float origAngle = super.getAngle();
        super.setAngle(0);
        
        super.translate(0,-500);
        super.move(_delta);

        super.setAngle(origAngle);
        super.rotate(rotation);
        super.move(_delta);
    }
}
