package com.bngames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;

public class Tile
{

	public static BufferedImage Tile_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage Tile_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	public static BufferedImage[] Tile_WALL2 =
	{ Game.spritesheet.getSprite(0, 96, 16, 16), Game.spritesheet.getSprite(16, 96, 16, 16) };
	public static BufferedImage[] Tile_CAVE =
	{ Game.spritesheet.getSprite(0, 80, 16, 16), Game.spritesheet.getSprite(16, 80, 16, 16) };

	public static boolean show = true;

	private BufferedImage sprite;
	private int x, y;

	public Tile(int x, int y, BufferedImage sprite)
	{
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}

	public void render(Graphics g)
	{

		if (show)
			g.drawImage(sprite, x - Camera.x, y - Camera.y, null);

	}
}
