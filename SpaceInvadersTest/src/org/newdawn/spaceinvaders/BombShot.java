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
public class BombShot extends ShotEntity{
    //How many degrees each update cycle the boomarang turns
    private float rotation;
    
    public BombShot(Game game, String sprite, int x, int y, double _speedMod, float _angle, float _rotation, int _damage, boolean _playerProj) {
        super(game, sprite, x, y, _speedMod, _angle, _damage, _playerProj);
        
        rotation = _rotation;
    }
    
    @Override
    public void move(long _delta){
        super.move(_delta);
    }
}
