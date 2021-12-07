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
package screen;

import world.*;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen extends RestartScreen {

	private boolean keyA = false, keyD = false, keyW = false, keyS = false, keyJ = false, keyK = false, keyO = false;
	private CommonMonster target;
	private int lastAction = 0;
	private int num = 10;
    private World world;
    private CreatureAI player;
    private List<CommonMonster> cms;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private List<String> oldMessages;
    
    private boolean allSee = false;
    
    private int continueTime = 2000;
    
    private int CDTime = 10000;
    
    private long beginTime = 0;
    
    private long curTime = 0;

    public PlayScreen(AsciiPanel terminal, JFrame app) {
    	super(terminal, app);
        this.screenHeight = 40;
        this.screenWidth = 70;
        createWorld();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        this.cms = new ArrayList<CommonMonster>(num);
        CreatureFactory creatureFactory = new CreatureFactory(this.world);
        createCreatures(creatureFactory);
        target = null;
        startGame();
    }
    
    private void startGame() {
    	 Thread threadPlayer = new Thread(this.player);
         threadPlayer.start();
         for(int i = 0; i < cms.size(); ++i) {
        	Thread threadCM = new Thread(this.cms.get(i));
         	threadCM.start();
         }
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        this.player = creatureFactory.newPlayer(this.messages);
        for(int i = 0; i < num; ++i) {
        	cms.add((CommonMonster) creatureFactory.newCommonMonster());
        }
        this.world.setCM(cms);
        this.world.setPlayer(player);
    }

    private void createWorld() {
    	world = new WorldBuilder(40, 70).makeMaps().build();
        //world = new WorldBuilder(40, 70).makeCaves().build();
    }

    private void displayTiles(int left, int top) {
    	terminal.write("                                                            ", 0 ,41);
    	terminal.write("press 'o' start total graph view CD:" + (int)(Math.min(((double)(curTime - beginTime))/CDTime, 1) * 100) + "%", 0, 41);
    	//show state
    	terminal.write("                                                            ", 0, 42);
    	terminal.write("player HP:" + player.hp() + "/" + player.maxHP(), 0, 42);
    	if(target != null) {
    		terminal.write("                                                        ", 0, 44);
    		terminal.write("Monster"+ target.id() + " HP:" + (target.hp() > 0 ? target.hp() : 0) + "/" + target.maxHP(), 0, 44);
    	}
        // Show terrain
        for (int y = 0; y < screenHeight && y < world.height(); y++) {
            for (int x = 0; x < screenWidth && x < world.width(); x++) {
                int wx = x + left;
                int wy = y + top;
                //terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
 
               	if (allSee || player.canSee(wx, wy)) {
                   	terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
               	} else {
                	terminal.write(world.glyph(wx, wy), x, y, Color.DARK_GRAY);
                }     
            }
        }
        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (creature.x() >= left && creature.x() < left + screenWidth && creature.y() >= top
                    && creature.y() < top + screenHeight) {
                //terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
            	if (allSee || player.canSee(creature.x(), creature.y())) {
                    terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
                }
            }
        }
        world.update();
        app.repaint();
    }

    //@Override
    public void displayOutput() {
    	terminal.clear();
        // Terrain and creatures
        displayTiles(getScrollX(), getScrollY());
        // Player
        int x = player.x();
        int y = player.y();
        terminal.write(player.glyph(), player.x() - getScrollX(), player.y() - getScrollY(), player.color());
    }
    
    public void shutDown() {
    	exit = true;
    }
    
    public void playerDo() {
    	if(keyO) {
    		if(curTime - beginTime > CDTime) {
    			allSee = true;
    			beginTime = curTime;
    		}
    	}
    	if(allSee) {
    		if(curTime - beginTime > continueTime) {
    			allSee = false;
    		}
    	}
    	player.setAttack(keyJ);
    	player.setJump(keyK);
    	int direction = 0;
    	for(int dir = lastAction, i = 0; i < 4; ++i) {
    		dir = dir % 4 + 1;
    		switch(dir) {
    		case PlayerAI.LEFT_ACTION:
    			if(keyA) {
    			direction += 1;
    			}
    			break;
    		case PlayerAI.RIGHT_ACTION:
    			if(keyD) {
    				direction += 2;
    			}
    			break;
    		case PlayerAI.UP_ACTION:
    			if(keyW) {
    				direction += 4;
    			}
    			break;
    		case PlayerAI.DOWN_ACTION:
    			if(keyS) {
    				direction += 8;
    			}
    			break;
    		}
    	}
    	player.setDirection(direction);
    }
//    
//    private synchronized void setA(boolean a) {
//    	keyA = a;
//    }
//    
//    private synchronized void setD(boolean a) {
//    	keyD = a;
//    }
//    
//    private synchronized void setW(boolean a) {
//    	keyW = a;
//    }
//    
//    private synchronized void setS(boolean a) {
//    	keyS = a;
//    }
//    
//    private synchronized void setJ(boolean a) {
//    	keyJ = a;
//    }
//    
//    private synchronized void setK(boolean a) {
//    	keyK = a;
//    }

    @Override
    public void pressKey(KeyEvent key) {
    	switch (key.getKeyCode()) {
        case KeyEvent.VK_A:
        	//setA(true);
        	keyA = true;
            break;
        case KeyEvent.VK_D:
        	//setD(true);
        	keyD = true;
            break;
        case KeyEvent.VK_W:
        	//setW(true);
        	keyW = true;
            break;
        case KeyEvent.VK_S:
        	//setS(true);
        	keyS = true;
            break;
        case KeyEvent.VK_J:
        	//setJ(true);
        	keyJ = true;
        	break;
        case KeyEvent.VK_K:
        	//setK(true);
        	keyK = true;
        	break;
        case KeyEvent.VK_O:
        	keyO = true;
        	break;
		default:
			break;
		}
    	playerDo();
    }
    
    @Override
    public void releaseKey(KeyEvent key) {
    	switch (key.getKeyCode()) {
        case KeyEvent.VK_A:
        	//setA(false);
        	keyA = false;
            break;
        case KeyEvent.VK_D:
        	//setD(false);
        	keyD = false;
            break;
        case KeyEvent.VK_W:
        	//setW(false);
        	keyW = false;
            break;
        case KeyEvent.VK_S:
        	//setS(false);
        	keyS = false;
            break;
        case KeyEvent.VK_J:
        	//setJ(false);
        	keyJ = false;
        	break;
        case KeyEvent.VK_K:
        	//setK(false);
        	keyK = false;
        	break;
        case KeyEvent.VK_O:
        	keyO = false;
        	break;
		default:
			break;
		}
    	playerDo();
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
    }
	
	@Override
	public void run() {
		while(!exit) {
			try {
				Thread.sleep(50);
				curTime = System.currentTimeMillis();
				displayOutput();
				target = world.setTarget();
		    	if(world.state() != World.GAME) {
		    		shutDown();
		    	}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(world.state() == World.WIN) {
    		link = new WinScreen(terminal, this.app);
    	}
    	else if(world.state() == World.LOSE) {
    		link = new LoseScreen(terminal, this.app);
    	}
		Thread linkThread = new Thread(link);
		linkThread.start();
	}

}
