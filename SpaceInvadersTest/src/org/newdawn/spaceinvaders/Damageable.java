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
public interface Damageable {
    public void die();
    public void takeDamage(int _damage);
}
