/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package world;

import java.awt.Color;

/**
 *
 * @author Aeranythe Echosong
 */
public class Creature {
	
	private int id;
	
	private static int maxDefense = 1000;

    private World world;
    
    private int maxHP;
    
    private int hp;
    
    private int attackValue;
    
    private int attackSpeed;
    
    private int moveSpeed;
    
    private int defenseValue;
    
    private int visionRadius;

    private int x;
    
    public int id() {
    	return id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int x() {
        return x;
    }

    private int y;

    public void setY(int y) {
        this.y = y;
    }

    public int y() {
        return y;
    }

    private char glyph;

    public char glyph() {
        return this.glyph;
    }

    private Color color;

    public Color color() {
        return this.color;
    }

    private CreatureAI ai;

    public void setAI(CreatureAI ai) {
        this.ai = ai;
    }
    
    public Tile tile(int wx, int wy) {
        return world.tile(wx, wy);
    }

    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    public boolean moveBy(int mx, int my) {
    	if(x+mx<0||y+my<0||x+mx>=world.width()||y+my>=world.height()) {
    		return false;
    	}
    	if(world.positionNoCorrupt(x + mx,y + my)) {//to do
	        return ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
    	}
    	else {
    		return false;
    	}
    }
    
    public int maxHP() {
    	return this.maxHP;
    }
    
    public int hp() {
    	return this.hp;
    }
    
    public void modifyHP(int amount) {
    	this.hp += amount;
    	if(this.hp < 1) {
    		world.remove(this);
    	}
    }
    
    public int attackSpeed() {
    	return this.attackSpeed;
    }
    
    public int attackValue() {
    	return this.attackValue;
    }
    
    public int moveSpeed() {
    	return this.moveSpeed;
    }
    
    public int defenseValue() {
    	return this.defenseValue;
    }
    
    public int visionRadius() {
    	return this.visionRadius;
    }
    
    public boolean canSee(int wx, int wy) {
    	return ai.canSee(wx,wy);
    }
    
    static public void attack(Creature other, int attackValue) {
    	int damage = Math.max(1, (int)(attackValue * ((double)maxDefense/(other.defenseValue + maxDefense))));
    	
    	other.modifyHP(-damage);
    }

    public boolean canEnter(int x, int y) {
        return world.tile(x, y).isGround();
    }

    public void notify(String message, Object... params) {
        ai.onNotify(String.format(message, params));
    }

    public Creature(World world, char glyph, Color color, int id, int maxHP, int aValue, int aSpeed, int dValue, int mSpeed, int visionRadius) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.id = id;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.attackValue = aValue;
        this.attackSpeed = aSpeed;
        this.moveSpeed = mSpeed;
        this.defenseValue = Math.min(maxDefense, Math.max(dValue, 1));
        this.visionRadius = visionRadius;
    }
}
