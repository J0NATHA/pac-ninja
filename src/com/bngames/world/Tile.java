package com.bngames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;

public class Tile
{
	public boolean show = true;
	private int x, y;
	private BufferedImage sprite;
	
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	
	public Tile(int x, int y, BufferedImage sprite)
	{
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	public static BufferedImage[] TILE_WALL2 =
	{ 
		Game.spritesheet.getSprite(0, 96, 16, 16), 
		Game.spritesheet.getSprite(16, 96, 16, 16) 
	};
	
	public static BufferedImage[] TILE_FLOOR2 =
	{ 
		Game.spritesheet.getSprite(0, 80, 16, 16), 
		Game.spritesheet.getSprite(16, 80, 16, 16) 
	};

	public void render(Graphics g)
	{
		if (show)
		{ g.drawImage(sprite, x - Camera.x, y - Camera.y, null); }
	}
}
