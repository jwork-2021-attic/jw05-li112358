package world;

import java.util.Random;

public class CommonMonster extends CreatureAI{  
	private boolean exit = false;
	
    private World world;
    
    private long attackTime;
    
    private long moveTime;
    
    Random ran;
    
    public static final int
   	LEFT_ACTION = 1,
   	RIGHT_ACTION = 2,
   	UP_ACTION = 3,
   	DOWN_ACTION = 4,
   	SLEEP = 5;
    
    private int action;

    public CommonMonster(Creature creature, World world) {
        super(creature);
        this.world = world;
        ran = new Random();
        this.action = 1;
    }
    
    public boolean See() {
    	
    	return true;
    }
    
    private void move() {
    	int x = world.getPlayerX(), y = world.getPlayerY();
    	if(canSee(x,y)) {
    		if(x > creature.x()) {
    			if(creature.moveBy(1, 0)) {
    				return;
    			}
    			else {
    				if(y > creature.y()) {
    					creature.moveBy(0, 1);
    				}
    				else {
    					creature.moveBy(0, -1);
    				}
    			}
    		}
    		else if(x < creature.x()) {
    			if(creature.moveBy(-1, 0)) {
    				return;
    			}
    			else {
    				if(y > creature.y()) {
    					creature.moveBy(0, 1);
    				}
    				else {
    					creature.moveBy(0, -1);
    				}
    			}
    		}
    		else{
    			if(y > creature.y()) {
					creature.moveBy(0, 1);
				}
				else {
					creature.moveBy(0, -1);
				}
    		}
    	}
    	else {
    		if(action != SLEEP)action = ran.nextInt(4) + 1;
    		for(int i = 0; i < 4; ++i) {
    			int mx = 0, my = 0;
	    		switch(action) {
	    		case LEFT_ACTION:{
	    			mx = -1;
	    			break;
	    		}
	    		case RIGHT_ACTION:{
	    			mx = 1;
	    			break;
	    		}
	    		case UP_ACTION:{
	    			my = -1;
	    			break;
	    		}
	    		case DOWN_ACTION:{
	    			my = 1;
	    			break;
	    		}
	    		default:{
	    			return;
	    		}
	    		}
	    		if(creature.moveBy(mx, my)) {
	    			return;
	    		}
	    		else {
	    			action = action % 4 + 1;
	    		}
    		}
    		action = SLEEP;
    	}
    }
    
    public void hunt() {
    	long curtime = System.currentTimeMillis();
    	int x = world.getPlayerX(), y = world.getPlayerY();
    	if(curtime - attackTime > creature.attackSpeed()) {
	    	if((x - creature.x())*(x - creature.x())+((y - creature.y())*(y - creature.y())) < 2) {
	    		attackTime = curtime;
	    		world.attackPlayer(creature.attackValue());
	    	}
    	}
    	if(curtime - moveTime > creature.moveSpeed()) {
    		moveTime = curtime;
    		move();
    	}
    }
    
    public void shutDown() {
    	exit = true;
    }

	@Override
	public void run() {
		while(!exit) {
			try {
				Thread.sleep(100);
				hunt();
				
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void beAttacked(int attackValue) {
		Creature.attack(creature, attackValue);
	}

	@Override
	public void setAttack(boolean b) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void setDirection(int direction) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void setJump(boolean k) {
		// TODO 自动生成的方法存根
		
	}
}
