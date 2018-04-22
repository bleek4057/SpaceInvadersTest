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
public class Alien_lvl2 extends AlienEntity{
    //How long the alien waits between firing shotss
    private float shotWait;
    
    public Alien_lvl2(Game game,String ref,int x,int y, float _shotWait) {
        super(game, ref,x,y);
        shotWait = _shotWait;
    }
}
