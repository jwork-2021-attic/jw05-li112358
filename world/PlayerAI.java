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

import java.util.List;

/**
 *
 * @author Aeranythe Echosong
 */
public class PlayerAI extends CreatureAI{
	
	private World world;
	
	private boolean attack;

	private int direction;
	
	private int shootdirection;
	
	private int lastdirection;
	
    private List<String> messages;
    
    private int attackSpeed;
    
    private int moveSpeed;
    
//    private long lastcodeTime;
//    
//    private int lastcode;
//    
//    private int code;
    
    private long moveTime;
    
    private long attackTime;
    
    private boolean jump;
    
    private boolean jumping;
    
    private int numJump;
    
    private int count;
    
    public static final int
    NO_ACTION = 0,
    L = 1,
    R = 2,
    U = 4,
    D = 8,
    LU = 5,
    RU = 6,
    LD = 9,
    RD = 10;
    
    public static final int
	LEFT_ACTION = 1,
	RIGHT_ACTION = 2,
	UP_ACTION = 3,
	DOWN_ACTION = 4,
	ATACK_ACTION = 5;
    

    public PlayerAI(Creature creature, World world) {
        super(creature);
        this.world = world;
        moveTime = System.currentTimeMillis();
        attackTime = System.currentTimeMillis();
        attackSpeed = creature.attackSpeed();
        moveSpeed = creature.moveSpeed();
        direction = NO_ACTION;
        lastdirection = NO_ACTION;
        shootdirection = R;
        Bullet.Set(creature.attackValue(), 50, creature.attackValue());
        attack = false;
        jump = false;
        jumping = false;
        numJump = 0;
        count = 0;
    }
    
    public synchronized void setAttack(boolean a) {
    	this.attack = a;
    }
    
    public synchronized void setDirection(int direction) {
    	switch(direction) {
		case L:case R:case U:case D:case LU:case RU:case LD:case RD:{
			this.shootdirection = direction;
			this.direction = direction;
			break;	
		}
		default:direction = NO_ACTION;
		}
    }

    public void onNotify(String message) {
        this.messages.add(message);
    }

//	public synchronized void setCode(int code) {
//		this.code = code;
//	}
	
	public void nextAttack() {
		int mx = 0, my = 0;
		switch(shootdirection) {
		case L:{
			mx = -1;
			break;	
		}
		case R:{
			mx = 1;
			break;
		}
		case U:{
			my = -1;
			break;
		}
		case D:{
			my = 1;
			break;
		}
		case LU:{
			mx = -1;
			my = -1;
			break;
		}
		case RU:{
			mx = 1;
			my = -1;
			break;
		}
		case LD:{
			mx = -1;
			my = 1;
			break;
		}
		case RD:{
			mx = 1;
			my = 1;
			break;
		}
		default:{
			System.out.println("no correct direction!!");
		}
		}
		new Thread(new Bullet(world,x()+mx,y()+my,shootdirection)).start();	
	}
	
	public void beAttacked(int attackValue) {
		Creature.attack(creature, attackValue);
	}
	
	public boolean move() {
		int mx = 0, my = 0;
		switch(direction) {
		case L:{
			mx = -1;
			lastdirection = L;
			break;	
		}
		case R:{
			mx = 1;
			lastdirection = R;
			break;
		}
		case LU:{
			mx = -1;
			lastdirection = L;
			break;
		}
		case RU:{
			mx = 1;
			lastdirection = R;
			break;
		}
		case LD:{
			mx = -1;
			lastdirection = L;
			break;
		}
		case RD:{
			mx = 1;
			lastdirection = R;
			break;
		}
		}
		direction = NO_ACTION;
		if(creature.moveBy(mx, my))
			return true;
		else
			return false;
	}
	
	public void setJump(boolean k) {
		jump = k;
	}
 	
	public void jump() {
		if(!jumping && jump && world.tile(creature.x(), creature.y()+1).isWall()) {
			jumping = true;
			numJump = 0;
			if(creature.moveBy(0,-1)) {
				numJump++;
				count = 0;
			}
			else
				jumping = false;
		}
		if(count == 2) {
			if(!jumping && !world.tile(creature.x(), creature.y()+1).isWall()) {
				creature.moveBy(0, 1);
			}
			if(jumping && jump && numJump < 3) {
				if(creature.moveBy(0, -1)) {
					numJump++;
				}
				else jumping = false;
			}
			else {
				jumping = false;
			}
			count = 0;
		}
		if(!jump) {
			jumping = false;
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(50);
				//System.out.println(direction);
				long now = System.currentTimeMillis();
				jump();
				count++;
				if(now - attackTime > attackSpeed && attack) {
					nextAttack();
					attackTime = now;
					setAttack(false);
				}
				else if(now - moveTime > moveSpeed && direction != NO_ACTION) {
					if(move())
						moveTime = now;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
