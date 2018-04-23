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
public class StraightShot extends ShotEntity {
    
    public StraightShot(Game game, String sprite, int x, int y, double _speedMod, float _angle, int _damage) {
        super(game, sprite, x, y, _speedMod, _angle, _damage);
    }
    
    @Override
    public void move(long _delta){
        super.move(_delta);
        
    }
    
}
