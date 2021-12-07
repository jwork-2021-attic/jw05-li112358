package world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
/**
 *
 * @author Aeranythe Echosong
 */
public class World {

    private Tile[][] tiles;
    private int width;
    private int height;
    private List<Creature> creatures;
    private List<CommonMonster> cms;
    private List<Bullet> bullets;
    
    private CreatureAI player;
    
    //private CreatureAI cm;
    
    private int state;
    
    private CommonMonster target;

    public static final int TILE_TYPES = 2;
    
    public static final int
    GAME = 0,
    WIN = 1,
    LOSE = 2;

    public World(Tile[][] tiles) {
        this.tiles = tiles;
        this.height = tiles.length;
        this.width = tiles[0].length;
        this.creatures = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.cms = new ArrayList<>();
        this.player = null;
        this.state = GAME;
    }

    public Tile tile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Tile.BOUNDS;
        } else {
            return tiles[y][x];
        }
    }
    
    public int state() {
    	return state;
    }
    
    public char glyph(int x, int y) {
        return tiles[y][x].glyph();
    }

    public Color color(int x, int y) {
        return tiles[y][x].color();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void dig(int x, int y) {
        if (tile(x, y).isDiggable()) {
            tiles[y][x] = Tile.FLOOR;
        }
    }
    
    private void addAtThisLocation(Creature creature, int x, int y) {
    	creature.setX(x);
    	creature.setY(y);
    	this.creatures.add(creature);
    }
    
    public void setPlayerLocation(Creature creature) {
    	addAtThisLocation(creature, 1, 38);
    }
    
    public void setCommonMonsterLocation(Creature creature) {
    	int x = 1, y = 1;
    	switch(creature.id()) {
    	case 1:x = 60; y = 37; break;
    	case 2:x = 63; y = 34; break;
    	case 3:x = 9; y = 32; break;
    	case 4:x = 10; y = 28; break;
    	case 5:x = 31; y = 29; break;
    	case 6:x = 37; y = 23; break;
    	case 7:x = 10; y = 10; break;
    	case 8:x = 60; y = 7; break;
    	case 9:x = 27; y = 15; break;
    	case 10:x = 10; y = 61; break;
    	default:System.out.println("uncorrect cm id");
    	}
    	addAtThisLocation(creature, x, y);
    }

    public void addAtEmptyLocation(Creature creature) {
        int x;
        int y;

        do {
            x = (int) (Math.random() * this.width);
            y = (int) (Math.random() * this.height);
        } while (!tile(x, y).isGround() || this.creature(x, y) != null);

        creature.setX(x);
        creature.setY(y);

        this.creatures.add(creature);
    }

    public Creature creature(int x, int y) {
        for (Creature c : this.creatures) {
            if (c.x() == x && c.y() == y) {
                return c;
            }
        }
        return null;
    }

    public List<Creature> getCreatures() {
        return this.creatures;
    }
    
    public List<CommonMonster> getCommonMonster(){
    	return this.cms;
    }
    
    public List<Bullet> getBullets(){
    	return this.bullets;
    }

    public void remove(Creature target) {
    	if(target.id() == CreatureFactory.PLAYER) {
    		state = LOSE;
    	}
    	else if(target.id() >= CreatureFactory.MONSTER) {
    		this.creatures.remove(target);
    		for(CommonMonster cm: cms) {
    			if(cm.id() == target.id()) {
    				cm.shutDown();
    				cms.remove(cm);
    				break;
    			}
    		}
    		if(cms.size() == 0)state = WIN;
    	}
    	else if(target.id() == CreatureFactory.BULLET) {
    		this.creatures.remove(target);
    		((Bullet) target).shutDown();
    	}
        this.creatures.remove(target);
    }

    public void update() {
    }
    
    public int[][] getMap(){
    	int[][] map = new int[this.tiles.length][];
    	for(int i = 0;i < this.tiles.length;++i) {
    		map[i] = new int[this.tiles[i].length];
    		for(int j = 0;j < this.tiles[i].length;++j) {
    			switch(this.tiles[i][j]) {
    			case WALL:map[i][j] = 0;break;
    			default:map[i][j] = 1;break;
    			}
    		}
    	}
    	return map;
    }
    
    public CommonMonster setTarget() {
    	return target;
    }
    
    public void setTile(int x, int y,Tile tile) {
    	this.tiles[y][x] = tile;
    }
    
    public void setPlayer(CreatureAI player) {
    	this.player = player;
    }
    
    public int getPlayerX() {
    	return player.x();
    }
    public int getPlayerY() {
    	return player.y();
    }
    
    public void setCM(List<CommonMonster> cms) {
    	this.cms = cms;
    	for(int i = 0; i < cms.size(); ++i) {
    		creatures.add(cms.get(i).creature);
    	}
    }
    
    public boolean positionNoCorrupt(int x, int y) {
    	if(player.x()!=x||player.y()!=y) {
    		for(CommonMonster cm : cms) {
    			if(cm.x() == x && cm.y() == y) {
    				return false;
    			}
    		}
    		return true;
    	}
    	else
    		return false;
    }
    
    public void attackPlayer(int attackValue) {
    	player.beAttacked(attackValue);
    }
    
    public boolean attackCM(int x, int y, int attackValue) {
    	for(CommonMonster cm : cms) {
    		if(cm.x() == x && cm.y() == y) {
    			cm.beAttacked(attackValue);
    			target = cm;
    			return true;
    		}
    	}
    	return false;
    }
    
    public void addBullet(Bullet bullet) {
    	this.creatures.add(bullet);
    	this.bullets.add(bullet);
    }
    
}
