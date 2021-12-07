package world;

import java.io.*;
import java.util.Random;

/*
 * Copyright (C) 2015 s-zhouj
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
 * @author s-zhouj
 */
public class WorldBuilder {

    private int width;
    private int height;
    int[][] map;
    private Tile[][] tiles;

    public WorldBuilder(int height, int width) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
    }

    public World build() {
        return new World(tiles);
    }
    
    void initMap() throws IOException {
    	String filePath = "resources/map.txt";
    	FileInputStream fin = new FileInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(fin);
    	BufferedReader bufferReader = new BufferedReader(reader);
    	String strTmp = "";
    	map = new int[height][];
    	int i = 0;
    	while((strTmp = bufferReader.readLine())!=null) {
    		if(i == 40)break;
    		map[i] = new int[width];
    		int size = strTmp.length();
    		for(int j = 0; j < 70; ++j) {
    			if(strTmp.charAt(j)=='1') {
    				map[i][j] = 1;
    			}
    			else {
    				map[i][j] = 0;
    			}
    		}
    		i++;
    	}
        bufferReader.close();
    }
    
    private WorldBuilder mapTiles() {
    	for(int i = 0; i < height; ++i) {
    		for(int j = 0; j < width; ++j) {
    			switch(map[i][j]) {
    			case 0:
                    tiles[i][j] = Tile.FLOOR;
                    break;
                case 1:
                    tiles[i][j] = Tile.WALL;
                    break;
    			}
    		}
    	}
    	return this;
    }

    private WorldBuilder randomizeTiles() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Random rand = new Random();
                switch (rand.nextInt(World.TILE_TYPES)) {
                    case 0:
                        tiles[y][x] = Tile.FLOOR;
                        break;
                    case 1:
                        tiles[y][x] = Tile.WALL;
                        break;
                }
            }
        }
        return this;
    }

    private WorldBuilder smooth(int factor) {
        Tile[][] newtemp = new Tile[height][width];
        if (factor > 1) {
            smooth(factor - 1);
        }
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                // Surrounding walls and floor
                int surrwalls = 0;
                int surrfloor = 0;

                // Check the tiles in a 3x3 area around center tile
                for (int dwidth = -1; dwidth < 2; dwidth++) {
                    for (int dheight = -1; dheight < 2; dheight++) {
                        if (y + dwidth < 0 || y + dwidth >= this.height || x + dheight < 0
                                || x + dheight >= this.width) {
                            continue;
                        } else if (tiles[y + dwidth][x + dheight] == Tile.FLOOR) {
                            surrfloor++;
                        } else if (tiles[y + dwidth][x + dheight] == Tile.WALL) {
                            surrwalls++;
                        }
                    }
                }
                Tile replacement;
                if (surrwalls > surrfloor) {
                    replacement = Tile.WALL;
                } else {
                    replacement = Tile.FLOOR;
                }
                newtemp[y][x] = replacement;
            }
        }
        tiles = newtemp;
        return this;
    }

    public WorldBuilder makeCaves() {
        return randomizeTiles().smooth(8);
    }
    
    public WorldBuilder makeMaps(){
    	try {
			initMap();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    	return mapTiles();
    }
}
