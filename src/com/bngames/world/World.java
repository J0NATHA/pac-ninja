package com.bngames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import com.bngames.entities.EnemySpectre;
import com.bngames.entities.EnemyShuriken;
import com.bngames.entities.Entity;
import com.bngames.entities.Particle;
import com.bngames.entities.ParticleBossHealth;
import com.bngames.entities.ParticlePickup;
import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.entities.SuperHealth;
import com.bngames.entities.Orb;
import com.bngames.graficos.Spritesheet;
import com.bngames.main.Game;
import com.bngames.main.SaveGame;

public class World
{
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	private static ArrayList<Orb> orbsBoss = new ArrayList<Orb>();

	public World(int level)
	{
		try
		{
			BufferedImage map = null;
			
			if(level > Game.MAX_LEVEL)
			{ level = 1; }
			
			level -= 1;
			while (map == null)
			{
				level++;

				try
				{
					map = ImageIO.read(getClass().getResource("/levels/level" + level + ".png"));
					Game.curLevel = level;
				} 
				
				catch (IOException e)
				{ continue; } 
				
				catch (IllegalArgumentException e)
				{ continue; }
			}

			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

			for (int xx = 0; xx < map.getWidth(); xx++)
			{
				for (int yy = 0; yy < map.getHeight(); yy++)
				{

					int pixelAtual = pixels[xx + (yy * map.getWidth())];
					
					ArrayList<Integer> surroundingPixels = new ArrayList<Integer>();
					
					add(surroundingPixels, pixels, xx + (yy * WIDTH) + 1);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) - 1);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) + WIDTH);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) - WIDTH);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) + WIDTH + 1);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) - WIDTH + 1);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) + WIDTH - 1);
					add(surroundingPixels, pixels, xx + (yy * WIDTH) - WIDTH - 1);
					
					if(Game.curLevel < 9)
					{ tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR); }

					else if(Game.curLevel >= 9)
					{
						int index = new Random().nextInt(2); 
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR2[index]);
					}

					if (pixelAtual == 0xFF000000)
					{
						// Chao
						if (Game.curLevel < 9)
						{ tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR); }
						
						else if (Game.curLevel >= 9)
						{
							int index = new Random().nextInt(2); 
							tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR2[index]);
						}
					} 
					else if (pixelAtual == 0xFFFFFFFF)
					{
// 						Wall
						if (Game.curLevel < 8)
						{
							tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
						} 
						
						else if(Game.curLevel == 8)
						{
							int matches = 0;
							
							for(int pixel : surroundingPixels)
							{
								if(pixel == 0xFFD63F35)
								{ ++matches; }
							}
							
							if(matches >= 2)
							{
								int index = new Random().nextInt(2); 
								tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL2[index]);
							}
							
							else
							{ tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL); }
						}
						
						else if (Game.curLevel >= 9)
						{
							int index = new Random().nextInt(2); 
							tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL2[index]);
						}
					}
					else if (pixelAtual == 0xFF4800FF)
					{
//						Player
						Game.player.setX(xx * 16);
						Game.player.setY(yy * 16);
					}
					else if (pixelAtual == 0xFFFFD800)
					{
						SuperHealth superHealth = new SuperHealth(
								(xx * 16) + 2, (yy * 16) + 2, 10, 10, Entity.SUPER_HEALTH);
						Game.entities.add(superHealth);
					}
					else if (pixelAtual == 0xFFFF0000)
					{
//						Spinning Shuriken
						EnemyShuriken enemy = new EnemyShuriken(xx * 16, yy * 16, 16, 16, 1, 
														Game.spritesheet.getSprite(2, 33, 13, 13));
						Game.entities.add(enemy);

					} 
					else if (pixelAtual == 0xFF15FF00)
					{
//						orb
						if (Game.curLevel != Game.MAX_LEVEL)
						{
							Orb orb = new Orb((xx * 16) + 3, (yy * 16) + 3, 8, 8, 0, Entity.ORB_SPRITE);
							Game.entities.add(orb);
							Game.orbContagem++;
							
							if(Game.curLevel == 8 && surroundingPixels.contains(0xFFD63F35))
							{
								int index = new Random().nextInt(2);
								tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR2[index]);
							}
						} 
						else if (Game.curLevel == Game.MAX_LEVEL)
						{
							Orb orb = new Orb((xx * 16) + 3, (yy * 16) + 3, 8, 8, 0, Entity.ORB_SPRITE);
							orbsBoss.add(orb);
						}

					} 
					else if (pixelAtual == 0xFFCB0002)
					{
//						Inimigo
						if (Game.curLevel != Game.MAX_LEVEL)
						{
							EnemySpectre en = new EnemySpectre(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
							Game.entities.add(en);

						} 
						else if (Game.curLevel == Game.MAX_LEVEL && Game.spawnEnemies == true)
						{
							if (Red.curLife == 3 && Game.enemies.size() == 0)
							{
								if (new Random().nextInt(100) < 50)
								{
									EnemySpectre en = new EnemySpectre(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
									Game.entities.add(en);
									Game.enemies.add(en);
								}
								if (Game.enemies.size() == 1)
									Game.spawnEnemies = false;
							} else if (Red.curLife == 2 && Game.enemies.size() < 2)
							{
								if (new Random().nextInt(100) < 50)
								{
									EnemySpectre en = new EnemySpectre(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
									Game.entities.add(en);
									Game.enemies.add(en);
								}
								if (Game.enemies.size() == 2)
									Game.spawnEnemies = false;
							} else if (Red.curLife == 1 && Game.enemies.size() < 2)
							{
								if (new Random().nextInt(100) < 50)
								{
									EnemySpectre en = new EnemySpectre(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
									Game.entities.add(en);
									Game.enemies.add(en);
									if (Game.enemies.size() == 3)
										Game.spawnEnemies = false;
								}
							}
						}

					}
					else if (pixelAtual == 0xFFD63F35)
					{
						int index = new Random().nextInt(2);
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR2[index]);
					}
					else if (pixelAtual == 0xFF00FFFF)
					{
						Entity en = new Red(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
						Game.entities.add(en);
					}

					if (Game.curLevel == Game.MAX_LEVEL)
					{
						int chance =(int)((1.1 - ((double)Red.curLife / Red.maxLife)) * 100);
						
						if (pixelAtual == 0xFFB200FF && new Random().nextInt(100) < chance)
						{
							int index = new Random().nextInt(2); 
							tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL2[index]);
						} 
					}
				}
			}
			
			if(Game.curLevel == Game.MAX_LEVEL)
			{
				while(Game.orbContagem < 20)
				{
					for(Orb orb : orbsBoss)
					{
						if(new Random().nextInt(100) < 90)
						{ continue; }
						
						Game.entities.add(orb);
						Game.orbContagem++;
						
						if(Game.orbContagem == 20)
						{ break; }
					}
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void generateParticle(int amount, int x, int y)
	{
		for (int i = 0; i < amount; i++)
		{
			Game.entities.add(new Particle(x, y, 1, 1, 4, null));
		}
	}
	
	public static void generatePickupParticle(int amount, int x, int y)
	{
		for (int i = 0; i < amount; i++)
		{
			Game.entities.add(new ParticlePickup(x, y, 2, 2, 1, null));
		}
	}

	public static void generateParticleBossHealth(int amount, int x, int y)
	{
		for (int i = 0; i < amount; i++)
		{
			Game.entities.add(new ParticleBossHealth(x, y, 3, 3, 1, null));
		}
	}
	
	private void add(ArrayList<Integer> surroundingPixels, int[] pixels, int position)
	{
		if(position >= 0 && position < pixels.length)
		{ surroundingPixels.add(pixels[position]); }
	}

	public static boolean isFree(int xnext, int ynext)
	{
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;

		int x2 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;

		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext + TILE_SIZE - 1) / TILE_SIZE;

		int x4 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (ynext + TILE_SIZE - 1) / TILE_SIZE;

		return !((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile)
				|| (tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile));
	}

	public static void restartGame(int level)
	{
		SaveGame.saveLastPlayedLevel(level);
		
		Game.orbAtual = 0;
		Game.orbContagem = 0;
		Game.orbsPicked = 0;

		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<EnemySpectre>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, 1, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World(level);
	}

	public void render(Graphics g)
	{
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;

		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);

		for (int xx = xstart; xx <= xfinal; xx++)
		{
			for (int yy = ystart; yy <= yfinal; yy++)
			{
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
				{ continue; }
				
				Tile tile = tiles[xx + (yy * WIDTH)];
			
				try 
				{ tile.render(g);}
				
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
	}
}
