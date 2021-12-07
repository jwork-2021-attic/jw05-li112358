package world;

import asciiPanel.AsciiPanel;

public class Bullet extends Creature implements Runnable {

	static int AttackValue;
	static int Speed;
	static int MaxDistance;
	
	private int lastTime;
	private int maxDistance;
	private int mx = 0, my = 0;
	private int distance = 0;
	private World world;

	private boolean exit = false;
	
	static public void Set(int attackValue, int speed, int maxDistance){
		Bullet.AttackValue = attackValue;
		Bullet.Speed = speed;
		Bullet.MaxDistance = maxDistance;
	}
	
	public Bullet(World world, int x, int y, int direction) {
		super(world,(char)7, AsciiPanel.brightWhite,-1,1,AttackValue,Speed,1, Speed, 0);
		this.world = world;
		this.maxDistance = MaxDistance;
		switch(direction) {
		case PlayerAI.L:mx = -1;break;
		case PlayerAI.R:mx = 1;break;
		case PlayerAI.U:my = -1;break;
		case PlayerAI.D:my = 1;break;
		case PlayerAI.LU:mx = -1;my = -1;break;
		case PlayerAI.RU:mx = 1;my = -1;break;
		case PlayerAI.LD:mx = -1;my = 1;break;
		case PlayerAI.RD:mx = 1;my = 1;break;
		}
		this.setX(x);
		this.setY(y);
		if(world.tile(x, y).isGround()) {
			world.addBullet(this);
		}
		else {
			return;
		}
		
	}
	
	public void shutDown() {
		exit = true;
	}
	
	
	@Override
	public void run() {
		while(!exit) {
			try {
				Thread.sleep(50);
				if(distance > maxDistance)this.world.remove(this);
				if(world.attackCM(x(), y(), attackValue())) {
					this.world.remove(this);
				}
				long curTime = System.currentTimeMillis();
				if(curTime - lastTime > this.moveSpeed()){
					if(world.tile(x()+mx,y()+my).isGround()) {
						this.setX(x()+mx);
						this.setY(y()+my);
						distance++;
					}
					else {
						this.world.remove(this);
					}
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
