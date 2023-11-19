package com.bngames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import com.bngames.entities.Enemy;
import com.bngames.entities.Enemy2;
import com.bngames.entities.Entity;
import com.bngames.entities.Particle;
import com.bngames.entities.Particle2;
import com.bngames.entities.Player;
import com.bngames.entities.Red;
import com.bngames.entities.Tree;
import com.bngames.graficos.Spritesheet;
import com.bngames.main.Game;

public class World
{

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;

	public World(int level)
	{

		try
		{
			BufferedImage map = null;
			level -= 1;
			while (map == null)
			{
				level++;

				try
				{
					map = ImageIO.read(getClass().getResource("/level" + level + ".png"));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
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
					if (Game.curLevel < 5)
					{
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_FLOOR);
					}

					else if (Game.curLevel >= 5)
					{
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_CAVE[0]);
					}

					if (pixelAtual == 0xFF000000)
					{
						// Chao
						if (Game.curLevel < 5)
							tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_FLOOR);
						else if (Game.curLevel >= 5)
						{
							if (new Random().nextInt(100) < 50)
								tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_CAVE[0]);
							else
								tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_CAVE[1]);
						}
					} else if (pixelAtual == 0xFFFFFFFF)
					{
// 						Parede
						if (Game.curLevel < 5)
						{
							tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL);
						} else if (Game.curLevel >= 5)
						{
							if (new Random().nextInt(100) < 50)
								tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL2[0]);
							else
								tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL2[1]);
						}
					} else if (pixelAtual == 0xFF4800FF)
					{
//							Player
						Game.player.setX(xx * 16);
						Game.player.setY(yy * 16);
					} else if (pixelAtual == 0xFFFF0000)
					{
//							Inimigo de fogo
						Enemy2 enemy = new Enemy2(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
						Game.entities.add(enemy);

					} else if (pixelAtual == 0xFF15FF00)
					{
//						tree
						if (Game.curLevel != 6)
						{
							Tree tree = new Tree((xx * 16) + 3, (yy * 16) + 3, 8, 8, 0, Entity.TREE_SPRITE);
							Game.entities.add(tree);
							Game.orbContagem++;
						} else if (Game.curLevel == 6 && Game.orbContagem < 20 && (new Random().nextInt(100) < 50))
						{
							Tree tree = new Tree((xx * 16) + 3, (yy * 16) + 3, 8, 8, 0, Entity.TREE_SPRITE);
							Game.entities.add(tree);
							Game.orbContagem++;
						}

					} else if (pixelAtual == 0xFFCB0002)
					{
//						Inimigo
						if (Game.curLevel != 6)
						{
							Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
							Game.entities.add(en);

						} else if (Game.curLevel == 6 && Game.spawnEnemies == true)
						{
							if (Red.curLife == 3 && Game.enemies.size() == 0)
							{
								if (new Random().nextInt(100) < 50)
								{
									Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
									Game.entities.add(en);
									Game.enemies.add(en);
								}
								if (Game.enemies.size() == 1)
									Game.spawnEnemies = false;
							} else if (Red.curLife == 2 && Game.enemies.size() < 2)
							{
								if (new Random().nextInt(100) < 50)
								{
									Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
									Game.entities.add(en);
									Game.enemies.add(en);
								}
								if (Game.enemies.size() == 2)
									Game.spawnEnemies = false;
							} else if (Red.curLife == 1 && Game.enemies.size() < 2)
							{
								if (new Random().nextInt(100) < 50)
								{
									Enemy en = new Enemy(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
									Game.entities.add(en);
									Game.enemies.add(en);
									if (Game.enemies.size() == 3)
										Game.spawnEnemies = false;
								}
							}
						}

					} else if (pixelAtual == 0xFFD63F35)
					{
						if (new Random().nextInt(100) < 50)
							tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_CAVE[0]);
						else
							tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.Tile_CAVE[1]);

					} else if (pixelAtual == 0xFF00FFFF)
					{
						Entity en = new Red(xx * 16, yy * 16, 16, 16, 1, Entity.ENEMY_EN);
						Game.entities.add(en);
					}

					if (Game.curLevel == 6)
					{
						if (new Random().nextInt(100) < 50)
						{
							if (pixelAtual == 0xFFB200FF)
							{
								if (new Random().nextInt(100) < 50)
									tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL2[0]);
								else
									tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL2[1]);
							}

						} else
						{
							if (pixelAtual == 0xFFFF007F)
							{
								if (new Random().nextInt(100) < 50)
									tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL2[0]);
								else
									tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.Tile_WALL2[1]);
							}
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void generateParticle(int amount, int x, int y)
	{
		for (int i = 0; i < amount; i++)
		{
			Game.entities.add(new Particle(x, y, 1, 1, 2, null));
		}
	}

	public static void generateParticle2(int amount, int x, int y)
	{
		for (int i = 0; i < amount; i++)
		{
			Game.entities.add(new Particle2(x, y, 1, 1, 1, null));
		}

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
		Game.orbAtual = 0;
		Game.orbContagem = 0;
		Game.orbsPicked = 0;

		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
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
					continue;
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}
	}
}
