package com.bngames.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.bngames.main.Game;

public class Tile {
	
	public static BufferedImage Tile_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage Tile_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	public static BufferedImage[] Tile_WALL2;
	public static BufferedImage[] Tile_CAVE;
	
	public static boolean show = true;
	
	private BufferedImage sprite;
	private int x,y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x=x;
		this.y=y;
		this.sprite=sprite;
		
		Tile_WALL2=new BufferedImage[2];
		Tile_WALL2[0] = Game.spritesheet.getSprite(0, 96, 16, 16);
		Tile_WALL2[1] = Game.spritesheet.getSprite(16, 96, 16, 16);
		
		Tile_CAVE=new BufferedImage[2];
		Tile_CAVE[0] = Game.spritesheet.getSprite(0,80, 16, 16);
		Tile_CAVE[1] = Game.spritesheet.getSprite(16,80, 16, 16);
				
	}
	
	public void render(Graphics g) {
		
	if(show)
		g.drawImage(sprite, x-Camera.x, y-Camera.y, null);
	
}
}
