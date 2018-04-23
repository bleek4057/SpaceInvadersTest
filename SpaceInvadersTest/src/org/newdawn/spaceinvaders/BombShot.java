/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.newdawn.spaceinvaders;

import java.util.ArrayList;

/**
 *
 * @author John
 */
public class BombShot extends ShotEntity{
    //How many degrees each update cycle the boomarang turns
    private float rotation;
    private ArrayList enemiesInRange;
    
    public BombShot(Game game, String sprite, int x, int y, double _speedMod, float _angle, float _rotation, int _damage, boolean _playerProj) {
        super(game, sprite, x, y, _speedMod, _angle, _damage, _playerProj);
        
        rotation = _rotation;
    }
    
    @Override
    public void move(long _delta){
        super.move(_delta);
    }
    
    @Override
    public void onDestroy(){
        enemiesInRange = super.game.findEnemyInRange(getX(), getY(), 100);
        
        for(int i = 0; i < enemiesInRange.size(); i++){
            AlienEntity enemy = (AlienEntity)enemiesInRange.get(i);
            enemy.takeDamage(1);
        }
    }
}
